package kardex.PEPS.InventoryPEPS.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kardex.PEPS.InventoryPEPS.application.ports.input.IProductQueryPort;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.port.output.IMessageServicePort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IProductQueryOutputPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.i18n.MessageKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductQueryService implements IProductQueryPort {
    private final IProductQueryOutputPort productQueryOutputPort;
    private final IMessageServicePort messageServicePort;
    
    @Override
    public List<Product> findAll(String enterpriseId) {
        log.info(messageServicePort.getMessage(MessageKeys.LOG_PRODUCT_QUERY_ALL, enterpriseId));
        return productQueryOutputPort.findAll(enterpriseId);
    }
    
}
