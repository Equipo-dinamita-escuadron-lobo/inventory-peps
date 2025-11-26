package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper;

import org.mapstruct.Mapper;

import kardex.PEPS.InventoryPEPS.domain.model.SyncState;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.SyncStateEntity;

@Mapper(componentModel = "spring")
/**
 * @brief Mapper for SyncState entity conversions
 * 
 * Handles transformation between SyncState domain model and SyncStateEntity.
 */
public interface ISyncStateEntityMapper {

    /**
     * @brief Converts entity to domain model
     * @param entity The SyncStateEntity
     * @return SyncState domain object
     */
    SyncState toDomain(SyncStateEntity entity);

    /**
     * @brief Converts domain model to entity
     * @param domain The SyncState domain object
     * @return SyncStateEntity
     */
    SyncStateEntity toEntity(SyncState domain);
}
