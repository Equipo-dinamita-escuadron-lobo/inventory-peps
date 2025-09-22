package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;

@Mapper(componentModel="spring")
public interface IKardexSaleReturnMapper {

    @Mapping(target = "detailsOrigin", ignore=true)
    @Mapping(target = "detailsOutput", ignore=true)
    @Mapping(target = "product", ignore=true)
    Kardex toDomain(KardexEntity kardexEntity);
    
}
