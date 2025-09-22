package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import kardex.PEPS.InventoryPEPS.domain.model.DetailOutput;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;

@Mapper(componentModel = "spring")
public interface IDetailOutputEntityMapper {

    @Mapping(target = "movementOrigin", ignore = true)
    @Mapping(target = "movementSale", ignore = true)
    DetailOutput toDomain(DetailOutputEntity entity);

  
    @Mapping(target = "movementSale", ignore = true)
    @Mapping(target = "movementOrigin", ignore = true)
    DetailOutputEntity toEntity(DetailOutput detailOutput);

    List<DetailOutputEntity> toEntityList(List<DetailOutput> details);
    List<DetailOutput> toDomainList(List<DetailOutputEntity> entities);
}
