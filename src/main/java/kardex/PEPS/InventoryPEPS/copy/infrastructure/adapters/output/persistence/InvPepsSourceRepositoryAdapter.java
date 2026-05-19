package kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.output.persistence;

import kardex.PEPS.InventoryPEPS.copy.application.output.IInvPepsSourceRepositoryPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IKardexRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Adaptador de salida: lectura de KardexEntity del tenant de origen.
 * Inventory-peps no tiene campo entId en KardexEntity (gestión por tenant separado en H2/PG).
 * Se retornan todos los kardex disponibles y se filtra por snapshotCorte vía date.
 * ADR-39.
 */
@Component
@RequiredArgsConstructor
public class InvPepsSourceRepositoryAdapter implements IInvPepsSourceRepositoryPort {

    private final IKardexRepository kardexRepository;

    @Override
    public List<KardexEntity> findByEntOrigenBeforeSnapshot(String entOrigen, Instant snapshotCorte) {
        return kardexRepository.findAll().stream()
                .filter(k -> k.getDate() == null
                        || !k.getDate().toInstant().isAfter(snapshotCorte))
                .toList();
    }
}
