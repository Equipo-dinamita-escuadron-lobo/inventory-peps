package kardex.PEPS.InventoryPEPS.copy.application;

import kardex.PEPS.InventoryPEPS.copy.application.output.ICopyJobLogRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IDetailOutputCopySourceRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IDetailOutputCopyTargetRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IInvPepsSourceRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.IInvPepsTargetRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.application.services.InvPepsCopyService;
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
import org.mockito.ArgumentCaptor;
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
 * Tests de remap FK para inventory-peps copy.
 * Verifica: Kardex idProduct remapeado en pasada 1; DetailOutput FKs remapeados en pasada 2.
 * RED phase: Fase 8.7.
 */
@ExtendWith(MockitoExtension.class)
class InvPepsCopyFkRemapTest {

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

    @Test
    @DisplayName("pasada 1: Kardex idProduct remapeado correctamente vía equivalenciasPrev tabla 'product'")
    void pasada1_kardex_idProductRemapeado() {
        UUID idProceso = UUID.randomUUID();

        CopyEquivalenciaDto eq = CopyEquivalenciaDto.builder()
                .modulo("products").tabla("product").idViejo("77").idNuevo("777").build();

        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso).fase(1)
                .entOrigen("ENT_A").entDestino("ENT_B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(List.of(eq))
                .build();

        when(logRepo.buscarPorIdProcesoYFase(idProceso.toString(), 1)).thenReturn(Optional.empty());

        ProductEntity product = new ProductEntity();
        product.setId(77L);
        product.setIdProduct(77L);
        product.setName("Prod77");
        product.setReference("R77");
        product.setPresentation("unit");
        product.setManager("mgr");

        KardexEntity k = new KardexEntity();
        k.setIdKardex(1L);
        k.setDate(ZonedDateTime.now());
        k.setDetails("d");
        k.setAmount(5);
        k.setUnitPrice(BigDecimal.TEN);
        k.setType(MovementType.COMPRA);
        k.setObjProduct(product);

        when(kardexSourceRepo.findByEntOrigenBeforeSnapshot(eq("ENT_A"), any())).thenReturn(List.of(k));

        ArgumentCaptor<KardexEntity> captor = ArgumentCaptor.forClass(KardexEntity.class);
        KardexEntity guardado = new KardexEntity();
        guardado.setIdKardex(101L);
        when(kardexTargetRepo.guardar(captor.capture())).thenReturn(guardado);

        when(detailSourceRepo.findByKardexIds(anyList())).thenReturn(List.of());

        CopyPhaseResponseDto response = service.ejecutar(request);

        assertThat(response.getEstado()).isEqualTo("COMPLETADO");
        assertThat(captor.getValue().getObjProduct().getIdProduct()).isEqualTo(777L);
    }

    @Test
    @DisplayName("pasada 2: DetailOutput movementSale y movementOrigin remapeados via mapa kardex pasada 1")
    void pasada2_detailOutput_fksRemapeados() {
        UUID idProceso = UUID.randomUUID();

        CopyEquivalenciaDto eq = CopyEquivalenciaDto.builder()
                .modulo("products").tabla("product").idViejo("10").idNuevo("110").build();

        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso).fase(1)
                .entOrigen("ENT_A").entDestino("ENT_B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(List.of(eq))
                .build();

        when(logRepo.buscarPorIdProcesoYFase(idProceso.toString(), 1)).thenReturn(Optional.empty());

        ProductEntity product = new ProductEntity();
        product.setId(10L);
        product.setIdProduct(10L);
        product.setName("Prod10");
        product.setReference("R10");
        product.setPresentation("unit");
        product.setManager("mgr");

        KardexEntity k = new KardexEntity();
        k.setIdKardex(5L);
        k.setDate(ZonedDateTime.now());
        k.setDetails("d");
        k.setAmount(10);
        k.setUnitPrice(BigDecimal.ONE);
        k.setType(MovementType.VENTA);
        k.setObjProduct(product);

        when(kardexSourceRepo.findByEntOrigenBeforeSnapshot(eq("ENT_A"), any())).thenReturn(List.of(k));

        KardexEntity kardexGuardado = new KardexEntity();
        kardexGuardado.setIdKardex(505L);
        when(kardexTargetRepo.guardar(any())).thenReturn(kardexGuardado);

        DetailOutputEntity detail = new DetailOutputEntity();
        detail.setIdDetailOutput(20L);
        detail.setAmountUsed(3);
        detail.setUnitPrice(BigDecimal.ONE);
        detail.setMovementSale(k);
        detail.setMovementOrigin(k);

        when(detailSourceRepo.findByKardexIds(anyList())).thenReturn(List.of(detail));

        ArgumentCaptor<DetailOutputEntity> captor = ArgumentCaptor.forClass(DetailOutputEntity.class);
        DetailOutputEntity detailGuardado = new DetailOutputEntity();
        detailGuardado.setIdDetailOutput(200L);
        when(detailTargetRepo.guardar(captor.capture())).thenReturn(detailGuardado);

        CopyPhaseResponseDto response = service.ejecutar(request);

        assertThat(response.getEstado()).isEqualTo("COMPLETADO");
        DetailOutputEntity captured = captor.getValue();
        assertThat(captured.getMovementSale().getIdKardex()).isEqualTo(505L);
        assertThat(captured.getMovementOrigin().getIdKardex()).isEqualTo(505L);
    }
}
