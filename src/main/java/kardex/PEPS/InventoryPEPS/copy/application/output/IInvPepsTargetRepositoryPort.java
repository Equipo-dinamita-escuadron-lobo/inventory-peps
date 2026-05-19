package kardex.PEPS.InventoryPEPS.copy.application.output;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;

/**
 * Puerto de salida: escritura de KardexEntity en el tenant de destino.
 * ADR-39.
 */
public interface IInvPepsTargetRepositoryPort {

    KardexEntity guardar(KardexEntity kardex);
}
