package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.dto.KardexRabbitDto;


@Mapper(componentModel = "spring")
public interface IKardexRabbitMQMapper {

    @Mapping(target = "product.productId", source = "productId")
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "idKardex", ignore = true)
    Kardex toDomain(KardexRabbitDto kardexRabbitDto);


    @Named("idToProduct")
    default Product mapIdToProduct(Long productId) {
        if (productId == null) {
            return null;
        }
        Product product = new Product();
        product.setProductId(productId); // Asignamos el ID al campo 'id' del producto
        return product;
    }



}
