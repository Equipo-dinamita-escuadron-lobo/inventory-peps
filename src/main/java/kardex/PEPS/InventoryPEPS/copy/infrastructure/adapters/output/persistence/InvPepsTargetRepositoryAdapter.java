package kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.output.persistence;

import kardex.PEPS.InventoryPEPS.copy.application.output.IInvPepsTargetRepositoryPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IKardexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida: escritura de KardexEntity en el tenant de destino.
 * ADR-39.
 */
@Component
@RequiredArgsConstructor
public class InvPepsTargetRepositoryAdapter implements IInvPepsTargetRepositoryPort {

    private final IKardexRepository kardexRepository;

    @Override
    public KardexEntity guardar(KardexEntity kardex) {
        return kardexRepository.save(kardex);
    }
}
