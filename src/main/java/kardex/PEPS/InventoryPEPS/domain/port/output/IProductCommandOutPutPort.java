package kardex.PEPS.InventoryPEPS.domain.port.output;

import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Product;

public interface IProductCommandOutPutPort {
    String saveAll(List<Product> products);
    String save(Product product);
}
