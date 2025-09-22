package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PutExchange;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockBuyDtoRequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockDtoResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockSellDtoRequest;

public interface IStockClient {
    @PutExchange("/api/stock/buy")
    ResponseEntity<ResponseDTO<StockDtoResponse>> buyStock(@RequestBody StockBuyDtoRequest stockDtoRequest);

    @PutExchange("/api/stock/sell")
    ResponseEntity<ResponseDTO<StockDtoResponse>> sellStock(@RequestBody StockSellDtoRequest stockDtoRequest);
    
} 
