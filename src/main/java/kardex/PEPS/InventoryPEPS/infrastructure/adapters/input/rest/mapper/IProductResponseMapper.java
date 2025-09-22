package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.ProductDTOResponse;

@Mapper(componentModel = "spring")
public interface IProductResponseMapper {
    
   //@Mapping(target = "recordsKardex", ignore=true)
    ProductDTOResponse toDtoResponse(Product product);
}
