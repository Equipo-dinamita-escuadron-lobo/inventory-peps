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

@Component
@RequiredArgsConstructor
@Slf4j
public class StockClientAdapter implements IStockClientPort{
    private final IStockClient stockClient;
    private final IStockClientMapper stockClientMapper;
    private final IFormatterResultOutputPort formatterResultOutputPort;

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
