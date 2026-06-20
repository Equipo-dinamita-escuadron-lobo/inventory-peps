package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PutExchange;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockBuyDtoRequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockDtoResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockSellDtoRequest;

/**
 * @brief HTTP client interface for Stock Service
 * 
 * Defines the contract for interacting with the external Stock Service
 * using Spring 6 HTTP Interfaces.
 */
public interface IStockClient {
    
    /**
     * @brief Increases stock quantity (Buy operation)
     * 
     * @param stockDtoRequest DTO containing purchase details
     * @return Response entity with the updated stock information
     */
    @PutExchange("/api/stock/buy")
    ResponseEntity<ResponseDTO<StockDtoResponse>> buyStock(@RequestBody StockBuyDtoRequest stockDtoRequest);

    /**
     * @brief Decreases stock quantity (Sell operation)
     * 
     * @param stockDtoRequest DTO containing sale details
     * @return Response entity with the updated stock information
     */
    @PutExchange("/api/stock/sell")
    ResponseEntity<ResponseDTO<StockDtoResponse>> sellStock(@RequestBody StockSellDtoRequest stockDtoRequest);
    
} 
