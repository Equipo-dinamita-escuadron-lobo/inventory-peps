package kardex.PEPS.InventoryPEPS.domain.port.output.external;

import java.time.Instant;
import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Product;

public interface IProductClientPort {
    List<Product> findAllProductsByEnterpriseId(String enterpriseId, Instant since);
}
