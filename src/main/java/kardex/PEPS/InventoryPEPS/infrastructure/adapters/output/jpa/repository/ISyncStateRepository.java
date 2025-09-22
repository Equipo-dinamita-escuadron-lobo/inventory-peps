package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.SyncStateEntity;

public interface ISyncStateRepository extends JpaRepository<SyncStateEntity, Long> {
    Optional<SyncStateEntity> findBySyncTypeAndEnterpriseId(String syncType, String enterpriseId);

    @Query("SELECT s.lastSyncDate FROM SyncStateEntity s WHERE s.syncType = :syncType AND s.enterpriseId = :enterpriseId")
    Optional<Instant> findLastSyncFor(String syncType, String enterpriseId);
}
