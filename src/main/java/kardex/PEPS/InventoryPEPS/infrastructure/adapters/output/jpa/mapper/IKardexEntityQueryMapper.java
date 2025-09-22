package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;

@Mapper(componentModel="spring", uses={IDetailOutputEntityMapper.class})
public interface IKardexEntityQueryMapper {
    
    @Mapping(target = "detailsOrigin", ignore=true)
    @Mapping(target = "detailsOutput", ignore=true)
    @Mapping(target = "product", ignore=true)
    Kardex toDomain(KardexEntity kardexEntity);

    List<Kardex> toDomainList(List<KardexEntity> KardexEntity);
}
