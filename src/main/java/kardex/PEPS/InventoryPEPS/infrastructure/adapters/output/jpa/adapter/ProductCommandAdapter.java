package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.function.Function;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.port.output.IProductCommandOutPutPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.ProductEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper.IProductEntityMapper;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.record.ProductPartition;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCommandAdapter implements IProductCommandOutPutPort {

    private final IProductRepository productRepository;
    private final IProductEntityMapper productEntityMapper;


    @Override
    public String saveAll(List<Product> products) {
        
        try{

            if (products.isEmpty()) {
                return "No products to process.";
            }
             // 1. Separate new products from existing ones
            ProductPartition partition = separateNewAndExistingProducts(products);

            // 2. Process both types of products
            int newCount = saveNewProducts(partition.newProducts());
            int updatedCount = updateExistingProducts(partition.existingProducts());

             // 3. Return result
            String message = String.format("Products processed: %d new, %d updated", newCount, updatedCount);
            log.info(message);
            return message;

        }catch (Exception e) {
            log.error("Error processing products", e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public String save(Product product) {
        try {
            ProductEntity productEntity = productEntityMapper.toEntity(product);
            productRepository.save(productEntity);
            return "Product saved successfully.";
        } catch (Exception e) {
            return "An error occurred while saving the product: " + e.getMessage();
        }
    }

    // Separate new products from existing ones
    private ProductPartition separateNewAndExistingProducts(List<Product> products) {
        List<Long> productIds = products.stream().map(Product::getProductId).toList();
        Set<Long> existingIds = new HashSet<>(productRepository.findProductsIdByProductIdIn(productIds));
        

        Map<Boolean, List<Product>> partitionedProducts = products.stream()
            .collect(Collectors.partitioningBy(p -> existingIds.contains(p.getProductId())));
    
        return new ProductPartition(
            partitionedProducts.get(false), 
            partitionedProducts.get(true)   
        );
    }

    // Save new products
    private int saveNewProducts(List<Product> newProducts) {
        if (newProducts.isEmpty()) {
            return 0;
        } 

         List<ProductEntity> newEntities = productEntityMapper.toEntity(newProducts);
        productRepository.saveAll(newEntities);
        
        log.info("Saved {} new products", newProducts.size());
        return newProducts.size();

    }

    // Update existing products
    private int updateExistingProducts(List<Product> existingProducts) {
        if (existingProducts.isEmpty()) {
            return 0;
        }

        List<Long> existingIds = existingProducts.stream().map(Product::getProductId).toList();
        List<ProductEntity> existingEntities = productRepository.findByProductIdIn(existingIds);

        Map<Long, Product> updateMap = existingProducts.stream()
            .collect(Collectors.toMap(Product::getProductId, Function.identity()));
        
        existingEntities.forEach(entity -> {
            Product product = updateMap.get(entity.getProductId());
            if (product != null) {
                productEntityMapper.updateEntityFromProduct(product, entity);
            }     
            
        });

        productRepository.saveAll(existingEntities);
        log.info("Updated {} existing products", existingProducts.size());
        return existingProducts.size();
    }

    @Transactional
    @Override
    public String deleteById(Long productId, String enterpriseId) {
        try {
            int deletedCount = productRepository.deleteByProductIdAndEnterpriseId(productId, enterpriseId);
            if (deletedCount > 0) {
                log.info("Product with ID {} deleted successfully for enterprise {}", productId, enterpriseId);
                return "Product deleted successfully.";
            } else {
                log.warn("Product with ID {} not found for enterprise {}", productId, enterpriseId);
                return "Product not found.";
            }
        } catch (Exception e) {
            log.error("Error deleting product with ID {} for enterprise {}: {}", productId, enterpriseId, e.getMessage());
            return "An error occurred while deleting the product: " + e.getMessage();
        }

    }

    @Override
    public String deleteAllByEnterpriseId(String enterpriseId) {
        try {
            int deletedCount = productRepository.deleteByEnterpriseId(enterpriseId);
            log.info("Deleted {} products for enterprise {}", deletedCount, enterpriseId);
            return String.format("Deleted %d products successfully.", deletedCount);
        } catch (Exception e) {
            log.error("Error deleting all products for enterprise {}: {}", enterpriseId, e.getMessage());
            return "An error occurred while deleting products: " + e.getMessage();
        }

    }



    
}
