package kardex.PEPS.InventoryPEPS.copy.application.output;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;

/**
 * Puerto de salida: escritura de DetailOutputEntity en el tenant de destino.
 * Pasada 2 del algoritmo DOS PASADAS FIFO — ADR-39.
 */
public interface IDetailOutputCopyTargetRepositoryPort {

    DetailOutputEntity guardar(DetailOutputEntity detail);
}
