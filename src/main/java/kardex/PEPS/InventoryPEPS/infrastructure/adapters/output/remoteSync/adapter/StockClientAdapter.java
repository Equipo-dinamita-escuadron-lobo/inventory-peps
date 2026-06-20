package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.adapter;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import kardex.PEPS.InventoryPEPS.domain.model.Stock;
import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.external.IStockClientPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IStockClient;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockDtoResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.mapper.IStockClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Adapter for Stock Service communication
 * 
 * Implements the output port to synchronize inventory changes with an external Stock Service.
 * Handles operations for increasing (buy) and decreasing (sell) stock levels.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StockClientAdapter implements IStockClientPort{
    private final IStockClient stockClient;
    private final IStockClientMapper stockClientMapper;
    private final IFormatterResultOutputPort formatterResultOutputPort;

    /**
     * @brief Updates external stock for a purchase or entry
     * 
     * Sends a request to the external stock service to increase the stock quantity
     * based on a purchase or positive adjustment.
     * 
     * @param stock The stock domain object containing quantity and product details
     */
    @Override
    public void buyStock(Stock stock) {

        try {
            ResponseEntity<ResponseDTO<StockDtoResponse>> response = stockClient.buyStock(stockClientMapper.toDtoRequest(stock));
            if (!response.getStatusCode().is2xxSuccessful()) {
                formatterResultOutputPort.returnErrorGenericResponse(response.getStatusCode().value(), "Failed to buy stock");
            }
        } catch (WebClientResponseException.ServiceUnavailable e) {
            log.warn("Stock service is unavailable (503)");
            formatterResultOutputPort.returnErrorGenericResponse(503, "Stock service is unavailable");
        }catch(WebClientResponseException e){
            log.warn("Error calling stock service");
            formatterResultOutputPort.returnErrorGenericResponse(500,"Error communicating with stock service");
        }catch(Exception e){
            log.error("Unexpected error when calling stock service");
            formatterResultOutputPort.returnErrorGenericResponse(500,"Unexpected error communicating with stock service");
        }
        
       
    }

    /**
     * @brief Updates external stock for a sale or exit
     * 
     * Sends a request to the external stock service to decrease the stock quantity
     * based on a sale or negative adjustment.
     * 
     * @param stock The stock domain object containing quantity and product details
     */
    @Override
    public void sellStock(Stock stock) {
        try {
            ResponseEntity<ResponseDTO<StockDtoResponse>> response = stockClient.sellStock(stockClientMapper.toSellDtoRequest(stock));
            if (!response.getStatusCode().is2xxSuccessful()) {
                formatterResultOutputPort.returnErrorGenericResponse(response.getStatusCode().value(), "Failed to sell stock");
            }
            
        } catch (WebClientResponseException.ServiceUnavailable e) {
            log.warn("Stock service is unavailable (503)");
            formatterResultOutputPort.returnErrorGenericResponse(503, "Stock service is unavailable");
        }catch(WebClientResponseException e){
            log.warn("Error calling stock service:{}-{}",e.getStatusCode(),e.getStatusText());
            formatterResultOutputPort.returnErrorGenericResponse(500,"Error communicating with stock service");

        }catch(Exception e){
            log.error("Unexpected error when calling stock service");
            formatterResultOutputPort.returnErrorGenericResponse(500,"Unexpected error communicating with stock service");
        }
    }
    
}
