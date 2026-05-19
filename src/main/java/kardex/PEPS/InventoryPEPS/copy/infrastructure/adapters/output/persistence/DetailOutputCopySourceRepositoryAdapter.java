package kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.output.persistence;

import kardex.PEPS.InventoryPEPS.copy.application.output.IDetailOutputCopySourceRepositoryPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IDetailOutPutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador de salida: lectura de DetailOutputEntity asociados a una lista de Kardex.
 * Pasada 2 del algoritmo DOS PASADAS FIFO — ADR-39.
 */
@Component
@RequiredArgsConstructor
public class DetailOutputCopySourceRepositoryAdapter implements IDetailOutputCopySourceRepositoryPort {

    private final IDetailOutPutRepository detailRepository;

    @Override
    public List<DetailOutputEntity> findByKardexIds(List<Long> kardexIds) {
        return detailRepository.findAll().stream()
                .filter(d -> (d.getMovementSale() != null && kardexIds.contains(d.getMovementSale().getIdKardex()))
                          || (d.getMovementOrigin() != null && kardexIds.contains(d.getMovementOrigin().getIdKardex())))
                .toList();
    }
}
