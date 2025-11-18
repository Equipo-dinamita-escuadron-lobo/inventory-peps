package kardex.PEPS.InventoryPEPS.domain.port.output.external;

import java.time.Instant;
import java.util.Optional;

import kardex.PEPS.InventoryPEPS.domain.model.SyncState;

public interface ISyncStateRepositoryPort {
     Optional<SyncState> findBySyncTypeAndEnterpriseId(String syncType, String enterpriseId);
     Optional<Instant> findLastSyncFor(String syncType, String enterpriseId);
     void save(SyncState syncState);
}
