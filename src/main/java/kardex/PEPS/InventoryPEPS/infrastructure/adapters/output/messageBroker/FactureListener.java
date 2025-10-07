package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexCommandPort;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.rabbitConfig.RabbitPEPSConfig;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.base.AbstractMessageListener;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.dto.EventDto;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.dto.KardexRabbitDto;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.enums.EventFactureType;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.mapper.IKardexRabbitMQMapper;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.rabbitmq.client.Channel;

@Component
@RequiredArgsConstructor
@Slf4j
public class FactureListener extends AbstractMessageListener<EventDto<KardexRabbitDto,EventFactureType>,EventFactureType> {
    
    private final IKardexCommandPort kardexCommandPort;
    private final IKardexRabbitMQMapper kardexRabbitMQMapper;
    
    @RabbitListener(queues = RabbitPEPSConfig.PEPS_QUEUE)
    public void handleFactureEvent( EventDto<KardexRabbitDto, EventFactureType> event,
        Message message, 
        Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        
        handleMessage(event, channel, deliveryTag);
    }

    @Override
    protected void processEvent(EventDto<KardexRabbitDto, EventFactureType> event) {
        switch (event.getType()) {
            case PURCHASE:
                Kardex kardex=kardexRabbitMQMapper.toDomain(event.getData());
                kardexCommandPort.registerPurchase(kardex);
                log.info("Registering purchase in Kardex for product ID: {}", kardex.getProduct().getProductId());
            break;
            case SALE:
                Kardex kardexSale=kardexRabbitMQMapper.toDomain(event.getData());
                kardexCommandPort.registerSale(kardexSale);
                log.info("Registering sale in Kardex for product ID: {}", kardexSale.getProduct().getProductId());
            break;
            case RETURNONSALE:
                Kardex kardexReturnOnSale=kardexRabbitMQMapper.toDomain(event.getData());
                kardexCommandPort.registerSaleReturn(kardexReturnOnSale);
                 log.info("Registering sale-return in Kardex for product ID: {}", kardexReturnOnSale.getProduct().getProductId());
            break;
            case RETURNONPURCHASE:
                Kardex kardexReturnOnPurchase=kardexRabbitMQMapper.toDomain(event.getData());
                kardexCommandPort.registerPurchaseReturn(kardexReturnOnPurchase);
                log.info("Registering purchase-return in Kardex for product ID: {}", kardexReturnOnPurchase.getProduct().getProductId());        
            break;
            case NONCOMMERCIALENTRY:
                Kardex nonCommercialEntry=kardexRabbitMQMapper.toDomain(event.getData());
                kardexCommandPort.registerNonCommercialEntry(nonCommercialEntry);
                log.info("Registering Non-commercial-Entry in Kardex for product ID: {}",nonCommercialEntry.getProduct().getProductId());

            break;
            case NONCOMMERCIALEXIT:
                Kardex nonCommercialExit=kardexRabbitMQMapper.toDomain(event.getData());
                kardexCommandPort.registerNonCommercialExit(nonCommercialExit);
                log.info("Registering Non-commercial-Exit in Kardex for product ID: {}");

            break;

            default:
                throw new IllegalArgumentException("Unsupported event type: " + event.getType());
        }

    }

    @Override
    protected boolean isValidEvent(EventDto<KardexRabbitDto, EventFactureType> event) {
        return event != null && event.getData() != null;
    }

    @Override
    protected String getEntityIdentifierSafely(EventDto<KardexRabbitDto, EventFactureType> event) {
       if (event == null || event.getData() == null) {
            return "unknown";
        }
        String factCode = event.getData().getFactCode() != null ? 
            event.getData().getFactCode().toString() : "unnamed";
        return factCode;
    }

    @Override
    protected String getEntityType() {
        return "Kardex";
    }


     @Override
    protected String extractEventType(EventDto<KardexRabbitDto, EventFactureType> event) {
        if (event == null) {
            return null;
        }
        
        return event.getType() != null ? event.getType().toString() : null;
    }

    @Override
    protected String convertEventToJson(EventDto<KardexRabbitDto, EventFactureType> event) {
        if (event == null) {
            return "{\"error\": \"Event is null\"}";
        }
        
        if (event.getData() == null) {
            return "{\"error\": \"Event data is null\", \"eventType\": \"" + 
                   (event.getType() != null ? event.getType().toString() : "null") + "\"}";
        }
        
        return JsonUtils.toJsonWithNullHandling(event.getData());
    }
    

}
