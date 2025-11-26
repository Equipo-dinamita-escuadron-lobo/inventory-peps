package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.adapter;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kardex.PEPS.InventoryPEPS.domain.model.SyncState;
import kardex.PEPS.InventoryPEPS.domain.port.output.external.ISyncStateRepositoryPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.SyncStateEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper.ISyncStateEntityMapper;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.ISyncStateRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
/**
 * @brief Adapter for SyncState operations
 * 
 * Implements the output port for managing synchronization state
 * persistence using JPA repository.
 */
public class SyncStatePortAdapter implements ISyncStateRepositoryPort {

    private final ISyncStateRepository syncStateRepository;
    private final ISyncStateEntityMapper syncStateEntityMapper;

    /**
     * @brief Finds synchronization state by type and enterprise
     * @param syncType The type of synchronization
     * @param enterpriseId The enterprise ID
     * @return Optional containing the sync state
     */
    @Override
    public Optional<SyncState> findBySyncTypeAndEnterpriseId(String syncType, String enterpriseId) {
        Optional<SyncStateEntity> entity = syncStateRepository.findBySyncTypeAndEnterpriseId(syncType, enterpriseId);
        return entity.map(syncStateEntityMapper::toDomain);
    }

    /**
     * @brief Finds the last synchronization timestamp
     * @param syncType The type of synchronization
     * @param enterpriseId The enterprise ID
     * @return Optional containing the last sync timestamp
     */
    @Override
    public Optional<Instant> findLastSyncFor(String syncType, String enterpriseId) {
        Optional<Instant> lastSync = syncStateRepository.findLastSyncFor(syncType, enterpriseId);
        return lastSync;
    }

    /**
     * @brief Saves a synchronization state
     * @param syncState The state to save
     */
    @Override
    public void save(SyncState syncState) {
        SyncStateEntity entity = syncStateEntityMapper.toEntity(syncState);
        syncStateRepository.save(entity);
    }
    
}
