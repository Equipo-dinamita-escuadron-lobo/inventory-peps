package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kardex.PEPS.InventoryPEPS.domain.model.DetailOutput;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;

@Mapper(componentModel = "spring")
public interface IDetailOutputEntityMapper {

    @Mapping(target="movementSale", ignore=true)
    @Mapping(target="movementOrigin", ignore=true)
    DetailOutput toDomain(DetailOutputEntity balanceEntity);
}
