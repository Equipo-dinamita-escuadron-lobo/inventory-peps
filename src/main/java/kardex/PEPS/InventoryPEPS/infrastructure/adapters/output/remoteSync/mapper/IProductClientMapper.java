package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.ProductSyncDto;

@Mapper(componentModel = "spring")
public interface IProductClientMapper {
    ProductSyncDto toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Product toDomain(ProductSyncDto dto);
}
