package kardex.PEPS.InventoryPEPS.domain.port.output.external;

import java.time.Instant;
import java.util.Optional;

import kardex.PEPS.InventoryPEPS.domain.model.SyncState;

/**
 * @brief Output port for synchronization state repository
 * 
 * Provides interface for persisting and retrieving the state of
 * data synchronization processes.
 */
public interface ISyncStateRepositoryPort {
     /**
      * @brief Finds synchronization state by type and enterprise
      * @param syncType Type of synchronization
      * @param enterpriseId Enterprise identifier
      * @return Optional containing sync state if found
      */
     Optional<SyncState> findBySyncTypeAndEnterpriseId(String syncType, String enterpriseId);

     /**
      * @brief Finds the last synchronization timestamp
      * @param syncType Type of synchronization
      * @param enterpriseId Enterprise identifier
      * @return Optional containing last sync timestamp if found
      */
     Optional<Instant> findLastSyncFor(String syncType, String enterpriseId);

     /**
      * @brief Saves synchronization state
      * @param syncState Synchronization state to save
      */
     void save(SyncState syncState);
}
