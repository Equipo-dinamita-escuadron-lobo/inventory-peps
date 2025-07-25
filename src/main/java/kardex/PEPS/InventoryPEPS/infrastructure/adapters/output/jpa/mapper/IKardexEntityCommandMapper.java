package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;

@Mapper(componentModel="Spring", uses={IDetailOutputEntityMapper.class})
public interface IKardexEntityCommandMapper {
    
    KardexEntity toEntity(Kardex kardex);

    @Mapping(target = "objProduct", ignore=true)
    Kardex toDomain(KardexEntity kardexEntity);
}

