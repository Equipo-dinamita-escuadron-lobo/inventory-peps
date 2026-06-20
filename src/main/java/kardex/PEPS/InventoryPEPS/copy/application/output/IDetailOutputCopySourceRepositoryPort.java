package kardex.PEPS.InventoryPEPS.copy.application.output;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;

import java.util.List;

/**
 * Puerto de salida: lectura de DetailOutputEntity asociados a una lista de Kardex.
 * Pasada 2 del algoritmo DOS PASADAS FIFO — ADR-39.
 */
public interface IDetailOutputCopySourceRepositoryPort {

    List<DetailOutputEntity> findByKardexIds(List<Long> kardexIds);
}
