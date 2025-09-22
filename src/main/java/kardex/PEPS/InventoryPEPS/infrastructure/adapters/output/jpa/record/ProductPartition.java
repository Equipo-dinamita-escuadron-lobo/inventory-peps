package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.record;

import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Product;

public record ProductPartition(List<Product> newProducts, List<Product> existingProducts) {} 
