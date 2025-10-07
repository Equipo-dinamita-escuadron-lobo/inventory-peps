package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper;



import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.ProductEntity;

@Mapper(componentModel = "spring")
public interface  IProductEntityMapper {

    @Mapping(target = "recordsKardex", ignore = true)
    Product toDomain(ProductEntity productEntity);

    @Mapping(target = "recordsKardex", ignore = true)
    ProductEntity toEntity(Product product);

    List<ProductEntity> toEntity(List<Product> products);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recordsKardex", ignore = true)
    void updateEntityFromProduct(Product product, @MappingTarget ProductEntity entity);


}
