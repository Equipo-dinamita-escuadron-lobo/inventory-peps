package kardex.PEPS.InventoryPEPS.domain.port.output;

import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Product;

public interface IProductCommandOutPutPort {
    String saveAll(List<Product> products);
    String save(Product product);

    /**
      * @brief Deletes a product by its ID and enterprise ID
      * @param productId Product ID to delete
      * @param enterpriseId Enterprise ID for context
      * @return Status message indicating operation result
      */
     String deleteById(Long productId, String enterpriseId);
     
     /**
      * @brief Deletes all products for a specific enterprise
      * @param enterpriseId Enterprise ID
      * @return Status message indicating operation result
      */
     String deleteAllByEnterpriseId(String enterpriseId);
}
