package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kardex.PEPS.InventoryPEPS.domain.model.DetailOutput;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;

@Mapper(componentModel="spring", uses={IKardexSaleReturnMapperImpl.class})
public interface IDetailOutPutSaleReturnMapper {



    
    DetailOutput toDomain(DetailOutputEntity entity);
    
    List<DetailOutput> toDomainList(List<DetailOutputEntity> entities);
    
    @Mapping(target = "movementSale", ignore = true)
    @Mapping(target = "movementOrigin", ignore = true)
    DetailOutputEntity toEntity(DetailOutput detailOutput);

}
