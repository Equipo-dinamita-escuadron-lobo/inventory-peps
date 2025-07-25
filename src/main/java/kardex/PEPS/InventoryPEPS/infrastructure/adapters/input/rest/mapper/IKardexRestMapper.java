package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexPurchaseDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexPurchaseDTOResponse;

@Mapper(componentModel ="spring")
public interface IKardexRestMapper {

    //@Mapping(target = "product.id", source = "idProduct")
    @Mapping(target = "date", ignore = true)
    //@Mapping(target = "id", ignore = true)
    Kardex toDomain(KardexPurchaseDTORequest kardexPurchaseDTORequest);

    KardexPurchaseDTOResponse toDTOResponse(Kardex kardex);
}
