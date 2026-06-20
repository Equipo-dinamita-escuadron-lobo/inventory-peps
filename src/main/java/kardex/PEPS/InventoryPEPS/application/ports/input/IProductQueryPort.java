package kardex.PEPS.InventoryPEPS.application.ports.input;

import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Product;

/**
 * @brief Input port for Product query operations
 * 
 * Provides query capabilities for retrieving product information
 * filtered by enterprise.
 */
public interface IProductQueryPort {
    /**
     * @brief Retrieves all products associated with a specific enterprise
     * @param enterpriseId Enterprise identifier
     * @return List of products belonging to the enterprise
     */
    List<Product> findAll(String enterpriseId);
}
