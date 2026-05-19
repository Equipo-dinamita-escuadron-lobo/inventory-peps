package kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.output.persistence;

import kardex.PEPS.InventoryPEPS.copy.application.output.IDetailOutputCopyTargetRepositoryPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IDetailOutPutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida: escritura de DetailOutputEntity en el tenant de destino.
 * Pasada 2 del algoritmo DOS PASADAS FIFO — ADR-39.
 */
@Component
@RequiredArgsConstructor
public class DetailOutputCopyTargetRepositoryAdapter implements IDetailOutputCopyTargetRepositoryPort {

    private final IDetailOutPutRepository detailRepository;

    @Override
    public DetailOutputEntity guardar(DetailOutputEntity detail) {
        return detailRepository.save(detail);
    }
}
