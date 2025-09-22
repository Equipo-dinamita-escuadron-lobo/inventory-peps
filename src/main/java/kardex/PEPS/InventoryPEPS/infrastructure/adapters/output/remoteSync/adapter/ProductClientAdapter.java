package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.adapter;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.port.output.IProductClientPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IProductClient;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.mapper.IProductClientMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductClientAdapter  implements IProductClientPort {
    private final IProductClientMapper productClientMapper;
    private final IProductClient productClient;


    @Override
    public List<Product> findAllProductsByEnterpriseId(String enterpriseId, Instant since) {
        return productClient.findAllProductsByEnterpriseId(enterpriseId, since)
            .stream()
            .map(productClientMapper::toDomain)
            .toList();
    }
    
}
