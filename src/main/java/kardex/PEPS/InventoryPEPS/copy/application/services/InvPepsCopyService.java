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
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    @Override
    public CopyPhaseResponseDto ejecutar(CopyPhaseRequestDto request) {
        // Validación básica
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
        logRepo.guardar(logInicio);

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
                nuevo.setAmount(original.getAmount());
                nuevo.setUnitPrice(original.getUnitPrice());
                nuevo.setType(original.getType());

                // Remapear ProductEntity: usar idProduct del original para lookup
                Long productIdOriginal = original.getObjProduct() != null
                        ? original.getObjProduct().getIdProduct()
                        : null;
                if (productIdOriginal != null) {
                    Long productIdNuevo = productIndex.get(productIdOriginal);
                    if (productIdNuevo == null) {
                        String adv = String.format(
                            "Kardex id=%s: idProduct=%s sin equivalencia en PRODUCTS; se inserta null.",
                            original.getIdKardex(), productIdOriginal);
                        log.warn(adv);
                        advertencias.add(adv);
                        nuevo.setObjProduct(null);
                    } else {
                        ProductEntity prodNuevo = new ProductEntity();
                        prodNuevo.setId(productIdNuevo);
                        prodNuevo.setIdProduct(productIdNuevo);
                        if (original.getObjProduct() != null) {
                            prodNuevo.setName(original.getObjProduct().getName());
                            prodNuevo.setReference(original.getObjProduct().getReference());
                            prodNuevo.setPresentation(original.getObjProduct().getPresentation());
                            prodNuevo.setManager(original.getObjProduct().getManager());
                        }
                        nuevo.setObjProduct(prodNuevo);
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
                    nuevoDetail.setAmountUsed(detail.getAmountUsed());
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
}
