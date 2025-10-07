package kardex.PEPS.InventoryPEPS.application.service.command;

import org.springframework.stereotype.Service;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.Stock;
import kardex.PEPS.InventoryPEPS.domain.port.output.IMessageServicePort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IStockClientPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.i18n.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockIntegrationService {
    private final IStockClientPort stockClient;
    private final IMessageServicePort messageService;

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
            log.info(messageService.getMessage(MessageKeys.LOG_STOCK_REQUEST_SUCCESS, 
                isBuy ? "purchase" : "sale"));
        } catch (Exception e) {
            log.error(messageService.getMessage(MessageKeys.LOG_STOCK_REQUEST_ERROR, 
                isBuy ? "purchase" : "sale", e.getMessage()));
        }
    }


    
}
