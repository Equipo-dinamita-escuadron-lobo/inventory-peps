package kardex.PEPS.InventoryPEPS.domain.port.output;

import java.util.List;
import java.util.Optional;

import kardex.PEPS.InventoryPEPS.domain.model.Product;

public interface IProductQueryOutputPort {

    Optional<Product> getProductByProductId(Long productId);
    List<Product> findAll(String enterpriseId);
}
