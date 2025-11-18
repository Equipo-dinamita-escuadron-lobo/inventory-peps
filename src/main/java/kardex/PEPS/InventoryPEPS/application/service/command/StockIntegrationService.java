package kardex.PEPS.InventoryPEPS.application.service.command;

import org.springframework.stereotype.Service;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.Stock;
import kardex.PEPS.InventoryPEPS.domain.port.output.external.IStockClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockIntegrationService {
    private final IStockClientPort stockClient;

     /**
     * Crea un objeto Stock basado en los datos del Kardex
     */
    public Stock createStock(Kardex kardex) {
        return Stock.builder()
            .productId(kardex.getProduct().getProductId())
            .quantity(kardex.getQuantity())
            .price(kardex.getUnitPrice())
            .build();
    }

    
    /**
     * Llama al servicio de stock para actualizar el inventario
     */
    public void callApiStockService(Stock stock, boolean isBuy) {
        try {
            if (isBuy) {
                stockClient.buyStock(stock);
            } else {
                stockClient.sellStock(stock);
            }
            log.info("{} stock updated successfully for product: {}", (isBuy ? "Purchase/Return" : "Sale"), stock.getProductId());
        } catch (Exception e) {
          log.warn("Failed to update stock for {} - Kardex will be saved but stock service was not synchronized: {}", 
                (isBuy ? "purchase" : "sale"), e.getMessage());
        }
    }


    
}
