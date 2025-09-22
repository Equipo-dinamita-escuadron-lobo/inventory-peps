package kardex.PEPS.InventoryPEPS.application.ports.input;

import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Product;

public interface IProductQueryPort {
    List<Product> findAll(String enterpriseId);
}
