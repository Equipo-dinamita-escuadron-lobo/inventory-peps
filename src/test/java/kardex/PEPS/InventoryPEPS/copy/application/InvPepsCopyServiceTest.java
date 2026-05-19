package kardex.PEPS.InventoryPEPS.copy.application;

import kardex.PEPS.InventoryPEPS.copy.application.output.ICopyJobLogRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IDetailOutputCopySourceRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IDetailOutputCopyTargetRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IInvPepsSourceRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IInvPepsTargetRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.services.InvPepsCopyService;
import kardex.PEPS.InventoryPEPS.copy.domain.enums.CopyEstado;
import kardex.PEPS.InventoryPEPS.copy.domain.models.CopyJobLog;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyEquivalenciaDto;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyPhaseRequestDto;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyPhaseResponseDto;
import kardex.PEPS.InventoryPEPS.domain.enums.MovementType;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para InvPepsCopyService.
 * Mockito standalone — sin contexto Spring.
 * RED phase: Fase 8.1.
 */
@ExtendWith(MockitoExtension.class)
class InvPepsCopyServiceTest {

    @Mock private ICopyJobLogRepositoryPort logRepo;
    @Mock private IInvPepsSourceRepositoryPort kardexSourceRepo;
    @Mock private IInvPepsTargetRepositoryPort kardexTargetRepo;
    @Mock private IDetailOutputCopySourceRepositoryPort detailSourceRepo;
    @Mock private IDetailOutputCopyTargetRepositoryPort detailTargetRepo;

    private InvPepsCopyService service;

    @BeforeEach
    void setUp() {
        service = new InvPepsCopyService(logRepo, kardexSourceRepo, kardexTargetRepo,
                detailSourceRepo, detailTargetRepo);
    }

    // ----------------------------------------------------------------
    // Escenario 1: copia 2 Kardex con remap idProduct (pasada 1)
    // ----------------------------------------------------------------

    @Test
    @DisplayName("copia 2 Kardex de origen a destino con remap idProduct — estado COMPLETADO")
    void copiar_dosKardex_conRemapProduct_retornaCompletado() {
        // Arrange
        UUID idProceso = UUID.randomUUID();

        CopyEquivalenciaDto eqProduct = CopyEquivalenciaDto.builder()
                .modulo("products")
                .tabla("product")
                .idViejo("10")
                .idNuevo("110")
                .build();

        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso)
                .fase(1)
                .entOrigen("ENT_A")
                .entDestino("ENT_B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(List.of(eqProduct))
                .build();

        when(logRepo.buscarPorIdProcesoYFase(idProceso.toString(), 1))
                .thenReturn(Optional.empty());

        ProductEntity product = buildProduct(10L, "idP_10");

        KardexEntity k1 = buildKardex(1L, product);
        KardexEntity k2 = buildKardex(2L, product);

        when(kardexSourceRepo.findByEntOrigenBeforeSnapshot(eq("ENT_A"), any()))
                .thenReturn(List.of(k1, k2));

        KardexEntity guardado1 = buildKardex(101L, product);
        KardexEntity guardado2 = buildKardex(102L, product);
        when(kardexTargetRepo.guardar(any())).thenReturn(guardado1, guardado2);

        when(detailSourceRepo.findByKardexIds(anyList())).thenReturn(List.of());

        // Act
        CopyPhaseResponseDto response = service.ejecutar(request);

        // Assert
        assertThat(response.getEstado()).isEqualTo("COMPLETADO");
        assertThat(response.getRegistrosProcesados()).isEqualTo(2);
        verify(kardexTargetRepo, times(2)).guardar(any());
    }

    // ----------------------------------------------------------------
    // Escenario 2: pasada 2 — DetailOutput con FK remapeados
    // ----------------------------------------------------------------

    @Test
    @DisplayName("pasada 2: DetailOutput copiado con movementSale y movementOrigin remapeados")
    void copiar_conDetailOutput_remapeaFKsKardex() {
        // Arrange
        UUID idProceso = UUID.randomUUID();

        CopyEquivalenciaDto eqProduct = CopyEquivalenciaDto.builder()
                .modulo("products").tabla("product").idViejo("10").idNuevo("110").build();

        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso)
                .fase(1)
                .entOrigen("ENT_A")
                .entDestino("ENT_B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(List.of(eqProduct))
                .build();

        when(logRepo.buscarPorIdProcesoYFase(idProceso.toString(), 1))
                .thenReturn(Optional.empty());

        ProductEntity product = buildProduct(10L, "idP_10");
        KardexEntity k1 = buildKardex(1L, product);

        when(kardexSourceRepo.findByEntOrigenBeforeSnapshot(eq("ENT_A"), any()))
                .thenReturn(List.of(k1));

        KardexEntity guardado1 = buildKardex(101L, product);
        when(kardexTargetRepo.guardar(any())).thenReturn(guardado1);

        // DetailOutput con movementSale=k1, movementOrigin=k1
        DetailOutputEntity detail = new DetailOutputEntity();
        detail.setIdDetailOutput(50L);
        detail.setAmountUsed(5);
        detail.setUnitPrice(BigDecimal.TEN);
        detail.setMovementSale(k1);
        detail.setMovementOrigin(k1);

        when(detailSourceRepo.findByKardexIds(anyList())).thenReturn(List.of(detail));

        DetailOutputEntity guardadoDetail = new DetailOutputEntity();
        guardadoDetail.setIdDetailOutput(500L);
        when(detailTargetRepo.guardar(any())).thenReturn(guardadoDetail);

        // Act
        CopyPhaseResponseDto response = service.ejecutar(request);

        // Assert
        assertThat(response.getEstado()).isEqualTo("COMPLETADO");
        // Verifica que se guardó el detail con FK remapeados
        verify(detailTargetRepo, times(1)).guardar(argThat(d ->
                d.getMovementSale() != null && d.getMovementSale().getIdKardex().equals(101L)
                && d.getMovementOrigin() != null && d.getMovementOrigin().getIdKardex().equals(101L)));
    }

    // ----------------------------------------------------------------
    // Escenario 3: idempotencia
    // ----------------------------------------------------------------

    @Test
    @DisplayName("idempotencia: retorna resultado previo si ya existe log — sin re-ejecutar")
    void copiar_idempotencia_retornaResultadoPrevio() {
        UUID idProceso = UUID.randomUUID();
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso).fase(1)
                .entOrigen("ENT_A").entDestino("ENT_B")
                .snapshotCorte(Instant.now()).build();

        CopyJobLog logPrevio = CopyJobLog.builder()
                .idProceso(idProceso).fase(1).modulo("invpeps")
                .estado(CopyEstado.COMPLETADO).equivalenciasGeneradas(5).build();

        when(logRepo.buscarPorIdProcesoYFase(idProceso.toString(), 1))
                .thenReturn(Optional.of(logPrevio));

        CopyPhaseResponseDto response = service.ejecutar(request);

        assertThat(response.getEstado()).isEqualTo("COMPLETADO");
        assertThat(response.getMensaje()).contains("idempotencia");
        verify(kardexSourceRepo, never()).findByEntOrigenBeforeSnapshot(any(), any());
    }

    // ----------------------------------------------------------------
    // Escenario 4: origen == destino → ERROR_NO_REINTENTABLE
    // ----------------------------------------------------------------

    @Test
    @DisplayName("origen igual a destino retorna ERROR_NO_REINTENTABLE")
    void copiar_origenIgualDestino_retornaError() {
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(UUID.randomUUID()).fase(1)
                .entOrigen("MISMA").entDestino("MISMA")
                .snapshotCorte(Instant.now()).build();

        CopyPhaseResponseDto response = service.ejecutar(request);

        assertThat(response.getEstado()).isEqualTo("ERROR_NO_REINTENTABLE");
        verify(kardexSourceRepo, never()).findByEntOrigenBeforeSnapshot(any(), any());
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private ProductEntity buildProduct(long externalId, String ref) {
        ProductEntity p = new ProductEntity();
        p.setId(externalId);
        p.setIdProduct(externalId);
        p.setName("Product " + ref);
        p.setReference(ref);
        p.setPresentation("unit");
        p.setManager("mgr");
        return p;
    }

    private KardexEntity buildKardex(Long id, ProductEntity product) {
        KardexEntity k = new KardexEntity();
        k.setIdKardex(id);
        k.setDate(ZonedDateTime.now());
        k.setDetails("detail");
        k.setAmount(10);
        k.setUnitPrice(BigDecimal.valueOf(100));
        k.setType(MovementType.COMPRA);
        k.setObjProduct(product);
        return k;
    }
}
