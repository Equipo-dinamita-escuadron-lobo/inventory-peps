package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper;

import org.mapstruct.Mapper;

import kardex.PEPS.InventoryPEPS.domain.model.SyncState;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.SyncStateEntity;

@Mapper(componentModel = "spring")
public interface ISyncStateEntityMapper {

    SyncState toDomain(SyncStateEntity entity);
    SyncStateEntity toEntity(SyncState domain);
}
