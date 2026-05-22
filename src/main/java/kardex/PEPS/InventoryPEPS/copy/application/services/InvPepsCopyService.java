package kardex.PEPS.InventoryPEPS.copy.application.services;

import kardex.PEPS.InventoryPEPS.copy.application.input.IExecuteInvPepsCopyPhasePort;
import kardex.PEPS.InventoryPEPS.copy.application.output.ICopyJobLogRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IDetailOutputCopySourceRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IDetailOutputCopyTargetRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IInvPepsSourceRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IInvPepsTargetRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.domain.enums.CopyEstado;
import kardex.PEPS.InventoryPEPS.copy.domain.models.CopyJobLog;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyEquivalenciaDto;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyPhaseRequestDto;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyPhaseResponseDto;
import kardex.PEPS.InventoryPEPS.domain.enums.MovementType;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.ProductEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación que orquesta la copia del módulo inventory-peps.
 *
 * Implementa ADR-39: DOS PASADAS FIFO.
 *
 * Pasada 1: copiar KardexEntity (con idProduct remapeado vía equivalenciasPrev tabla "product")
 *            → registrar equivalencia oldKardexId → newKardexId en mapa interno.
 *
 * Pasada 2: copiar DetailOutputEntity (con movementSale y movementOrigin remapeados
 *            vía mapa kardex construido en pasada 1).
 *
 * ADR-38, ADR-39.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class InvPepsCopyService implements IExecuteInvPepsCopyPhasePort {

    private static final String MODULO = "invpeps";

    private final ICopyJobLogRepositoryPort logRepo;
    private final IInvPepsSourceRepositoryPort kardexSourceRepo;
    private final IInvPepsTargetRepositoryPort kardexTargetRepo;
    private final IDetailOutputCopySourceRepositoryPort detailSourceRepo;
    private final IDetailOutputCopyTargetRepositoryPort detailTargetRepo;
    private final IProductRepository productRepository;

    @Override
    public CopyPhaseResponseDto ejecutar(CopyPhaseRequestDto request) {
        if (request.getDatosImportados() != null) {
            return ejecutarImportacion(request);
        }
        if (request.getEntDestino() == null || request.getEntDestino().isBlank()) {
            return ejecutarExportacion(request);
        }
        if (request.getEntOrigen().equals(request.getEntDestino())) {
            return CopyPhaseResponseDto.builder()
                    .estado("ERROR_NO_REINTENTABLE")
                    .mensaje("entOrigen y entDestino no pueden ser iguales")
                    .equivalenciasGeneradas(Collections.emptyList())
                    .advertencias(Collections.emptyList())
                    .build();
        }

        String idProceso = request.getIdProceso().toString();

        // Idempotencia: si ya existe log para este proceso+fase, retornar resultado previo
        Optional<CopyJobLog> previo = logRepo.buscarPorIdProcesoYFase(idProceso, request.getFase());
        if (previo.isPresent()) {
            log.info("Fase {} del proceso {} ya fue ejecutada — retornando resultado previo (idempotencia)",
                    request.getFase(), idProceso);
            return construirResponseDesdeLog(previo.get());
        }

        // Registrar inicio
        CopyJobLog logInicio = CopyJobLog.builder()
                .idProceso(request.getIdProceso())
                .fase(request.getFase())
                .modulo(MODULO)
                .estado(CopyEstado.EN_PROCESO)
                .fechaInicio(Instant.now())
                .equivalenciasGeneradas(0)
                .build();

        // Construir índice product: idExterno (idProduct field) → nuevo productEntity
        Map<Long, Long> productIndex = construirIndiceProducto(request.getEquivalenciasPrev());

        List<String> advertencias = new ArrayList<>();
        List<CopyEquivalenciaDto> equivalencias = new ArrayList<>();

        // Mapa kardex: oldKardexId → nueva KardexEntity (para pasada 2)
        Map<Long, KardexEntity> kardexEquivalenciaMap = new HashMap<>();

        int totalRegistros = 0;

        try {
            // ---- PASADA 1: copiar KardexEntity ----
            List<KardexEntity> origenList = kardexSourceRepo.findByEntOrigenBeforeSnapshot(
                    request.getEntOrigen(), request.getSnapshotCorte());

            for (KardexEntity original : origenList) {
                KardexEntity nuevo = new KardexEntity();
                nuevo.setIdKardex(null);
                nuevo.setDate(original.getDate());
                nuevo.setDetails(original.getDetails());
                nuevo.setQuantity(original.getQuantity());
                nuevo.setUnitPrice(original.getUnitPrice());
                nuevo.setType(original.getType());

                // Remapear ProductEntity: usar idProduct del original para lookup
                Long productIdOriginal = original.getProduct() != null
                        ? original.getProduct().getProductId()
                        : null;
                if (productIdOriginal != null) {
                    Long productIdNuevo = productIndex.get(productIdOriginal);
                    if (productIdNuevo == null) {
                        String adv = String.format(
                            "Kardex id=%s: idProduct=%s sin equivalencia en PRODUCTS; se inserta null.",
                            original.getIdKardex(), productIdOriginal);
                        log.warn(adv);
                        advertencias.add(adv);
                        nuevo.setProduct(null);
                    } else {
                        ProductEntity prodNuevo = new ProductEntity();
                        prodNuevo.setId(productIdNuevo);
                        prodNuevo.setProductId(productIdNuevo);
                        if (original.getProduct() != null) {
                            prodNuevo.setName(original.getProduct().getName());
                            prodNuevo.setReference(original.getProduct().getReference());
                            prodNuevo.setPresentation(original.getProduct().getPresentation());
                        }
                        nuevo.setProduct(prodNuevo);
                    }
                }

                KardexEntity guardado = kardexTargetRepo.guardar(nuevo);
                kardexEquivalenciaMap.put(original.getIdKardex(), guardado);

                equivalencias.add(CopyEquivalenciaDto.builder()
                        .modulo(MODULO)
                        .tabla("kardex")
                        .idViejo(String.valueOf(original.getIdKardex()))
                        .idNuevo(String.valueOf(guardado.getIdKardex()))
                        .build());
                totalRegistros++;
            }

            // ---- PASADA 2: copiar DetailOutputEntity con FK remapeados ----
            List<Long> kardexIdsOriginales = origenList.stream()
                    .map(KardexEntity::getIdKardex)
                    .collect(Collectors.toList());

            if (!kardexIdsOriginales.isEmpty()) {
                List<DetailOutputEntity> detalles = detailSourceRepo.findByKardexIds(kardexIdsOriginales);

                for (DetailOutputEntity detail : detalles) {
                    DetailOutputEntity nuevoDetail = new DetailOutputEntity();
                    nuevoDetail.setIdDetailOutput(null);
                    nuevoDetail.setQuantityUsed(detail.getQuantityUsed());
                    nuevoDetail.setUnitPrice(detail.getUnitPrice());

                    // Remapear movementSale
                    KardexEntity nuevoSale = remapearKardex(
                            detail.getMovementSale(), kardexEquivalenciaMap, advertencias, "movementSale");
                    nuevoDetail.setMovementSale(nuevoSale);

                    // Remapear movementOrigin
                    KardexEntity nuevoOrigin = remapearKardex(
                            detail.getMovementOrigin(), kardexEquivalenciaMap, advertencias, "movementOrigin");
                    nuevoDetail.setMovementOrigin(nuevoOrigin);

                    detailTargetRepo.guardar(nuevoDetail);
                }
            }

        } catch (Exception e) {
            log.error("Error inesperado durante copia invpeps del proceso {}: {}", idProceso, e.getMessage(), e);
            registrarFallo(request, e.getMessage(), logInicio.getFechaInicio());
            return CopyPhaseResponseDto.builder()
                    .estado("ERROR_REINTENTABLE")
                    .mensaje("Error interno: " + e.getMessage())
                    .equivalenciasGeneradas(Collections.emptyList())
                    .advertencias(Collections.emptyList())
                    .build();
        }

        CopyEstado estadoFinal = advertencias.isEmpty()
                ? CopyEstado.COMPLETADO
                : CopyEstado.COMPLETADO_CON_ADVERTENCIAS;

        CopyJobLog logFin = CopyJobLog.builder()
                .idProceso(request.getIdProceso())
                .fase(request.getFase())
                .modulo(MODULO)
                .estado(estadoFinal)
                .fechaInicio(logInicio.getFechaInicio())
                .fechaFin(Instant.now())
                .equivalenciasGeneradas(equivalencias.size())
                .build();
        logRepo.guardar(logFin);

        return CopyPhaseResponseDto.builder()
                .estado(estadoFinal.name())
                .registrosProcesados(totalRegistros)
                .equivalenciasGeneradas(equivalencias)
                .mensaje("Copia inventory-peps completada exitosamente")
                .advertencias(advertencias)
                .build();
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    /**
     * Construye índice idProduct (campo idProduct en ProductEntity) → nuevo id
     * desde equivalenciasPrev tabla "product".
     */
    private Map<Long, Long> construirIndiceProducto(List<CopyEquivalenciaDto> equivalenciasPrev) {
        if (equivalenciasPrev == null) return Collections.emptyMap();
        return equivalenciasPrev.stream()
                .filter(e -> "product".equals(e.getTabla())
                             && e.getIdViejo() != null && e.getIdNuevo() != null)
                .collect(Collectors.toMap(
                        e -> Long.parseLong(e.getIdViejo()),
                        e -> Long.parseLong(e.getIdNuevo()),
                        (a, b) -> a));
    }

    /**
     * Remapea un KardexEntity FK usando el mapa de kardex construido en pasada 1.
     * Si el FK no tiene equivalencia: registra advertencia y retorna null.
     */
    private KardexEntity remapearKardex(KardexEntity original,
                                         Map<Long, KardexEntity> kardexMap,
                                         List<String> advertencias,
                                         String campo) {
        if (original == null) return null;
        KardexEntity nuevo = kardexMap.get(original.getIdKardex());
        if (nuevo == null) {
            String adv = String.format(
                "DetailOutput: %s kardexId=%s sin equivalencia; se inserta null.",
                campo, original.getIdKardex());
            log.warn(adv);
            advertencias.add(adv);
        }
        return nuevo;
    }

    private CopyPhaseResponseDto construirResponseDesdeLog(CopyJobLog log) {
        return CopyPhaseResponseDto.builder()
                .estado(log.getEstado().name())
                .registrosProcesados(log.getEquivalenciasGeneradas() != null ? log.getEquivalenciasGeneradas() : 0)
                .equivalenciasGeneradas(Collections.emptyList())
                .mensaje("Resultado de ejecución previa (idempotencia)")
                .advertencias(Collections.emptyList())
                .build();
    }

    private void registrarFallo(CopyPhaseRequestDto request, String mensaje, Instant fechaInicio) {
        try {
            CopyJobLog logFallo = CopyJobLog.builder()
                    .idProceso(request.getIdProceso())
                    .fase(request.getFase())
                    .modulo(MODULO)
                    .estado(CopyEstado.FALLIDO)
                    .fechaInicio(fechaInicio != null ? fechaInicio : Instant.now())
                    .fechaFin(Instant.now())
                    .equivalenciasGeneradas(0)
                    .errorMessage(mensaje)
                    .build();
            logRepo.guardar(logFallo);
        } catch (Exception e) {
            log.error("Error al registrar fallo de copia invpeps: {}", e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Modo BACKUP: exportar datos del entOrigen como payload serializable
    // ----------------------------------------------------------------

    private CopyPhaseResponseDto ejecutarExportacion(CopyPhaseRequestDto request) {
        log.info("Modo BACKUP — exportando invpeps para entOrigen={}", request.getEntOrigen());
        try {
            List<KardexEntity> kardexList = kardexSourceRepo.findByEntOrigenBeforeSnapshot(
                    request.getEntOrigen(), request.getSnapshotCorte());

            List<Long> kardexIds = kardexList.stream()
                    .map(KardexEntity::getIdKardex)
                    .collect(Collectors.toList());

            List<DetailOutputEntity> details = kardexIds.isEmpty()
                    ? Collections.emptyList()
                    : detailSourceRepo.findByKardexIds(kardexIds);

            List<Map<String, Object>> kardexMaps = new ArrayList<>();
            for (KardexEntity k : kardexList) {
                Map<String, Object> km = new LinkedHashMap<>();
                km.put("idKardex", k.getIdKardex());
                km.put("factCode", k.getFactCode());
                km.put("date", k.getDate() != null ? k.getDate().toString() : null);
                km.put("details", k.getDetails());
                km.put("quantity", k.getQuantity());
                km.put("unitPrice", k.getUnitPrice() != null ? k.getUnitPrice().toPlainString() : null);
                km.put("type", k.getType() != null ? k.getType().name() : null);
                km.put("availableQuantity", k.getAvailableQuantity());
                // Exportar productId externo (campo en ProductEntity) para remapeo posterior
                Long productIdExterno = k.getProduct() != null ? k.getProduct().getProductId() : null;
                km.put("productId", productIdExterno);
                kardexMaps.add(km);
            }

            List<Map<String, Object>> detailMaps = new ArrayList<>();
            for (DetailOutputEntity d : details) {
                Map<String, Object> dm = new LinkedHashMap<>();
                dm.put("idDetailOutput", d.getIdDetailOutput());
                dm.put("quantityUsed", d.getQuantityUsed());
                dm.put("unitPrice", d.getUnitPrice() != null ? d.getUnitPrice().toPlainString() : null);
                dm.put("movementSaleId", d.getMovementSale() != null ? d.getMovementSale().getIdKardex() : null);
                dm.put("movementOriginId", d.getMovementOrigin() != null ? d.getMovementOrigin().getIdKardex() : null);
                detailMaps.add(dm);
            }

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("kardex", kardexMaps);
            payload.put("details", detailMaps);

            int total = kardexMaps.size() + detailMaps.size();
            log.info("BACKUP invpeps: {} kardex, {} details exportados", kardexMaps.size(), detailMaps.size());

            return CopyPhaseResponseDto.builder()
                    .estado("COMPLETADO")
                    .registrosProcesados(total)
                    .equivalenciasGeneradas(Collections.emptyList())
                    .mensaje("Modo BACKUP — datos exportados correctamente")
                    .advertencias(Collections.emptyList())
                    .datosExportados(payload)
                    .build();
        } catch (Exception e) {
            log.error("Error durante exportación invpeps: {}", e.getMessage(), e);
            return CopyPhaseResponseDto.builder()
                    .estado("ERROR_REINTENTABLE")
                    .mensaje("Error en exportación: " + e.getMessage())
                    .equivalenciasGeneradas(Collections.emptyList())
                    .advertencias(Collections.emptyList())
                    .build();
        }
    }

    // ----------------------------------------------------------------
    // Modo RESTORE: importar datos desde payload serializado
    // ----------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private CopyPhaseResponseDto ejecutarImportacion(CopyPhaseRequestDto request) {
        log.info("Modo RESTORE — importando invpeps para entDestino={}", request.getEntDestino());

        String idProceso = request.getIdProceso().toString();

        // Idempotencia
        Optional<CopyJobLog> previo = logRepo.buscarPorIdProcesoYFase(idProceso, request.getFase());
        if (previo.isPresent()) {
            log.info("RESTORE fase {} proceso {} ya ejecutada — retornando resultado previo", request.getFase(), idProceso);
            return construirResponseDesdeLog(previo.get());
        }

        CopyJobLog logInicio = CopyJobLog.builder()
                .idProceso(request.getIdProceso())
                .fase(request.getFase())
                .modulo(MODULO)
                .estado(CopyEstado.EN_PROCESO)
                .fechaInicio(Instant.now())
                .equivalenciasGeneradas(0)
                .build();

        List<String> advertencias = new ArrayList<>();
        List<CopyEquivalenciaDto> equivalencias = new ArrayList<>();
        int totalRegistros = 0;

        try {
            Map<String, Object> datos = (Map<String, Object>) request.getDatosImportados();
            List<Map<String, Object>> kardexMaps = (List<Map<String, Object>>) datos.get("kardex");
            List<Map<String, Object>> detailMaps = (List<Map<String, Object>>) datos.get("details");

            // Índice productId externo → nuevo productId externo (desde equivalenciasPrev)
            Map<Long, Long> productIndex = construirIndiceProducto(request.getEquivalenciasPrev());

            // Pasada 1: importar KardexEntity
            Map<Long, KardexEntity> kardexOldToNew = new HashMap<>(); // oldIdKardex → nueva KardexEntity

            if (kardexMaps != null) {
                for (Map<String, Object> km : kardexMaps) {
                    Long oldIdKardex = toLong(km.get("idKardex"));
                    Long oldProductIdExterno = toLong(km.get("productId"));

                    KardexEntity nuevo = new KardexEntity();
                    nuevo.setIdKardex(null);
                    nuevo.setFactCode(toStr(km.get("factCode")));
                    nuevo.setDate(toZonedDateTime(km.get("date")));
                    nuevo.setDetails(toStr(km.get("details")));
                    nuevo.setQuantity(toInt(km.get("quantity")));
                    nuevo.setUnitPrice(toBigDecimal(km.get("unitPrice")));
                    nuevo.setType(toMovementType(km.get("type")));
                    nuevo.setAvailableQuantity(toInt(km.get("availableQuantity")));

                    // Remapear product usando equivalenciasPrev
                    if (oldProductIdExterno != null) {
                        Long newProductIdExterno = productIndex.get(oldProductIdExterno);
                        if (newProductIdExterno == null) {
                            String adv = "Kardex id=" + oldIdKardex + ": productId=" + oldProductIdExterno
                                    + " sin equivalencia en PRODUCTS; product=null.";
                            log.warn(adv);
                            advertencias.add(adv);
                            nuevo.setProduct(null);
                        } else {
                            // Buscar ProductEntity local por productId externo remapeado
                            Optional<ProductEntity> prodOpt = productRepository.findByProductId(newProductIdExterno);
                            if (prodOpt.isPresent()) {
                                nuevo.setProduct(prodOpt.get());
                            } else {
                                String adv = "Kardex id=" + oldIdKardex + ": ProductEntity con productId="
                                        + newProductIdExterno + " no encontrada en destino; product=null.";
                                log.warn(adv);
                                advertencias.add(adv);
                                nuevo.setProduct(null);
                            }
                        }
                    }

                    KardexEntity guardado = kardexTargetRepo.guardar(nuevo);
                    if (oldIdKardex != null) {
                        kardexOldToNew.put(oldIdKardex, guardado);
                    }

                    equivalencias.add(CopyEquivalenciaDto.builder()
                            .modulo(MODULO)
                            .tabla("kardex")
                            .idViejo(String.valueOf(oldIdKardex))
                            .idNuevo(String.valueOf(guardado.getIdKardex()))
                            .build());
                    totalRegistros++;
                }
            }

            // Pasada 2: importar DetailOutputEntity con FKs remapeados
            if (detailMaps != null) {
                for (Map<String, Object> dm : detailMaps) {
                    Long oldSaleId = toLong(dm.get("movementSaleId"));
                    Long oldOriginId = toLong(dm.get("movementOriginId"));

                    DetailOutputEntity nuevoDetail = new DetailOutputEntity();
                    nuevoDetail.setIdDetailOutput(null);
                    nuevoDetail.setQuantityUsed(toInt(dm.get("quantityUsed")));
                    nuevoDetail.setUnitPrice(toBigDecimal(dm.get("unitPrice")));

                    // Remapear movementSale
                    KardexEntity nuevoSale = remapearKardex(oldSaleId, kardexOldToNew, advertencias, "movementSale");
                    nuevoDetail.setMovementSale(nuevoSale);

                    // Remapear movementOrigin
                    KardexEntity nuevoOrigin = remapearKardex(oldOriginId, kardexOldToNew, advertencias, "movementOrigin");
                    nuevoDetail.setMovementOrigin(nuevoOrigin);

                    detailTargetRepo.guardar(nuevoDetail);
                    totalRegistros++;
                }
            }

        } catch (Exception e) {
            log.error("Error durante importación invpeps proceso {}: {}", idProceso, e.getMessage(), e);
            registrarFallo(request, e.getMessage(), logInicio.getFechaInicio());
            return CopyPhaseResponseDto.builder()
                    .estado("ERROR_REINTENTABLE")
                    .mensaje("Error en importación: " + e.getMessage())
                    .equivalenciasGeneradas(Collections.emptyList())
                    .advertencias(Collections.emptyList())
                    .build();
        }

        CopyEstado estadoFinal = advertencias.isEmpty()
                ? CopyEstado.COMPLETADO
                : CopyEstado.COMPLETADO_CON_ADVERTENCIAS;

        CopyJobLog logFin = CopyJobLog.builder()
                .idProceso(request.getIdProceso())
                .fase(request.getFase())
                .modulo(MODULO)
                .estado(estadoFinal)
                .fechaInicio(logInicio.getFechaInicio())
                .fechaFin(Instant.now())
                .equivalenciasGeneradas(equivalencias.size())
                .build();
        logRepo.guardar(logFin);

        return CopyPhaseResponseDto.builder()
                .estado(estadoFinal.name())
                .registrosProcesados(totalRegistros)
                .equivalenciasGeneradas(equivalencias)
                .mensaje("RESTORE inventory-peps completado")
                .advertencias(advertencias)
                .build();
    }

    // ----------------------------------------------------------------
    // Helpers de conversión de tipos (JSON → Java)
    // ----------------------------------------------------------------

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(v.toString()); } catch (NumberFormatException ignored) { return null; }
    }

    private int toInt(Object v) {
        if (v instanceof Integer i) return i;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(v.toString()); } catch (NumberFormatException ignored) { return 0; }
    }

    private String toStr(Object v) {
        return v != null ? v.toString() : null;
    }

    private BigDecimal toBigDecimal(Object v) {
        if (v == null) return null;
        if (v instanceof BigDecimal bd) return bd;
        try { return new BigDecimal(v.toString()); } catch (NumberFormatException ignored) { return null; }
    }

    private ZonedDateTime toZonedDateTime(Object v) {
        if (v == null) return null;
        try { return ZonedDateTime.parse(v.toString()); } catch (Exception ignored) { return null; }
    }

    private MovementType toMovementType(Object v) {
        if (v == null) return null;
        try { return MovementType.valueOf(v.toString()); } catch (IllegalArgumentException ignored) { return null; }
    }

    private KardexEntity remapearKardex(Long oldKardexId, Map<Long, KardexEntity> kardexMap,
                                         List<String> advertencias, String campo) {
        if (oldKardexId == null) return null;
        KardexEntity nuevo = kardexMap.get(oldKardexId);
        if (nuevo == null) {
            String adv = "DetailOutput: " + campo + " kardexId=" + oldKardexId + " sin equivalencia; se inserta null.";
            log.warn(adv);
            advertencias.add(adv);
        }
        return nuevo;
    }

    private Long remapearProductId(Long idViejo, Map<Long, Long> productIndex,
                                    List<String> advertencias, Long entidadId) {
        if (idViejo == null) return null;
        Long idNuevo = productIndex.get(idViejo);
        if (idNuevo == null) {
            String adv = "productId=" + idViejo + " sin equivalencia para entidad id=" + entidadId;
            log.warn(adv);
            advertencias.add(adv);
        }
        return idNuevo;
    }
}
