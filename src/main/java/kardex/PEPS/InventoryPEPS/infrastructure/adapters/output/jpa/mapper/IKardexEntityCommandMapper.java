package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;

@Mapper(componentModel="spring", uses={IDetailOutputEntityMapper.class})
public interface IKardexEntityCommandMapper {
    
    @Mapping(target = "detailsOutput", ignore = true)
    @Mapping(target = "detailsOrigin", ignore = true)
    @Mapping(target = "product", ignore = true)
    KardexEntity toEntity(Kardex kardex);

    @Mapping(target = "detailsOutput", ignore = true)
    @Mapping(target = "detailsOrigin", ignore = true)
    @Mapping(target = "product", ignore = true)
    Kardex toDomain(KardexEntity kardexEntity);

    // Método específico para mapear Kardex con detalles para registro de ventas
    @Mapping(target = "product", ignore = true)
    KardexEntity toEntityForSale(Kardex kardex);

    List<Kardex> listToDomain(List<KardexEntity> listKardex);
}

