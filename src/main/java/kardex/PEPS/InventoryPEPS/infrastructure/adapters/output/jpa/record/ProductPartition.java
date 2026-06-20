package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.record;

import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Product;

/**
 * @brief Record for partitioning products during synchronization
 * 
 * Holds separated lists of new products (to be inserted) and existing products
 * (to be updated) to optimize batch processing.
 * 
 * @param newProducts List of products that don't exist in the database
 * @param existingProducts List of products that already exist in the database
 */
public record ProductPartition(List<Product> newProducts, List<Product> existingProducts) {} 
