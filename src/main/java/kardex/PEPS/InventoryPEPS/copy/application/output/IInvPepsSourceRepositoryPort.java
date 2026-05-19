package kardex.PEPS.InventoryPEPS.copy.application.output;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;

import java.time.Instant;
import java.util.List;

/**
 * Puerto de salida: lectura de KardexEntity del tenant de origen.
 * ADR-39.
 */
public interface IInvPepsSourceRepositoryPort {

    List<KardexEntity> findByEntOrigenBeforeSnapshot(String entOrigen, Instant snapshotCorte);
}
