package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.port.output.query.IProductQueryOutputPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper.IProductEntityMapper;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor
public class ProductQueryAdapter implements IProductQueryOutputPort{

    private final IProductRepository productRepository;
    private final IProductEntityMapper productEntityMapper;

    @Override
    public Optional<Product> getProductByProductId(Long productId) {
          return productRepository.findByProductId(productId)
                .map(productEntityMapper::toDomain); 

                
    }

    @Override
    public List<Product> findAll(String enterpriseId) {
         return productRepository.findAllByEnterpriseId(enterpriseId).stream()
                .map(productEntityMapper::toDomain)
                .toList(); 
    }
    
}
