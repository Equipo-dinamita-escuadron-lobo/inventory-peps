package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.SyncStateEntity;

/**
 * @brief Repository for SyncState entities
 * 
 * Handles database operations for synchronization state tracking.
 */
public interface ISyncStateRepository extends JpaRepository<SyncStateEntity, Long> {
    /**
     * @brief Finds sync state by type and enterprise
     * @param syncType The type of synchronization
     * @param enterpriseId The enterprise ID
     * @return Optional containing the sync state
     */
    Optional<SyncStateEntity> findBySyncTypeAndEnterpriseId(String syncType, String enterpriseId);

    /**
     * @brief Finds the last synchronization timestamp
     * @param syncType The type of synchronization
     * @param enterpriseId The enterprise ID
     * @return Optional containing the last sync timestamp
     */
    @Query("SELECT s.lastSyncDate FROM SyncStateEntity s WHERE s.syncType = :syncType AND s.enterpriseId = :enterpriseId")
    Optional<Instant> findLastSyncFor(String syncType, String enterpriseId);
}
