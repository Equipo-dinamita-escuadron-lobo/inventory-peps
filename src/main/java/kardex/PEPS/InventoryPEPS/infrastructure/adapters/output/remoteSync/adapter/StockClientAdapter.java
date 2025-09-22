package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.adapter;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import kardex.PEPS.InventoryPEPS.domain.model.Stock;
import kardex.PEPS.InventoryPEPS.domain.port.output.IStockClientPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IStockClient;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockDtoResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.mapper.IStockClientMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockClientAdapter implements IStockClientPort{
    private final IStockClient stockClient;
    private final IStockClientMapper stockClientMapper;

    @Override
    public void buyStock(Stock stock) {
        ResponseEntity<ResponseDTO<StockDtoResponse>> response = stockClient.buyStock(stockClientMapper.toDtoRequest(stock));
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }
        throw new RuntimeException("Failed to buy stock");
    }

    @Override
    public void sellStock(Stock stock) {
        ResponseEntity<ResponseDTO<StockDtoResponse>> response = stockClient.sellStock(stockClientMapper.toSellDtoRequest(stock));
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }
        throw new RuntimeException("Failed to sell stock");
    }
    
}
