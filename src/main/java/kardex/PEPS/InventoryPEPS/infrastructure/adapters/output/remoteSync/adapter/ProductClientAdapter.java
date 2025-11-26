package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.adapter;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.external.IProductClientPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IProductClient;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.mapper.IProductClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Adapter for Product Service communication
 * 
 * Implements the output port to synchronize product data from an external Product Service.
 * Used to fetch product updates based on a timestamp.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClientAdapter  implements IProductClientPort {
    private final IProductClientMapper productClientMapper;
    private final IProductClient productClient;
    private final IFormatterResultOutputPort formatterResultOutputPort;

    /**
     * @brief Retrieves all products for an enterprise modified after a specific time
     * 
     * Queries the external product service for products associated with the enterprise
     * that have been created or modified since the provided timestamp.
     * 
     * @param enterpriseId The unique identifier of the enterprise
     * @param since The timestamp to filter products modified after this time
     * @return List of Product domain objects, or an empty list on error
     */
    @Override
    public List<Product> findAllProductsByEnterpriseId(String enterpriseId, Instant since) {
        try {
            return productClient.findAllProductsByEnterpriseId(enterpriseId, since)
            .stream()
            .map(productClientMapper::toDomain)
            .toList();

        } catch (WebClientResponseException.ServiceUnavailable e) {
            log.warn("Product service is unavailable (503)");
            formatterResultOutputPort.returnErrorGenericResponse(503, "Product service is unavailable");    
        }catch(WebClientResponseException e){
            log.warn("Error calling product service");
            formatterResultOutputPort.returnErrorGenericResponse(500,"Error communicating with product service");
        }catch(Exception e){
            log.error("Unexpected error when calling product service");
            formatterResultOutputPort.returnErrorGenericResponse(500,"Unexpected error communicating with product service");    
        }
        return List.of();
    }
    
}
