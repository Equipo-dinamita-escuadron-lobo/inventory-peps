package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import kardex.PEPS.InventoryPEPS.domain.port.output.IProductEventPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.rabbitConfig.RabbitProductUsedConfig;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.dto.EventDto;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.dto.ProductUsageEventDto;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.enums.EventUsageType;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.aspect.JwtTokenService;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher implements IProductEventPort{

    private final RabbitTemplate rabbitTemplate;
    private final JwtTokenService jwtTokenService;

    @Override
    public void publishUsedProductEvent(Long productId, Integer quantityUsed) {
        ProductUsageEventDto event = new ProductUsageEventDto();
        event.setProductId(productId);
        event.setQuantityUsed(quantityUsed);

        EventDto<ProductUsageEventDto, EventUsageType> eventDto = new EventDto<>(event, EventUsageType.USED);
        log.info("Publishing product created event, productId: {}, quantityUsed: {}", productId, quantityUsed);

        rabbitTemplate.convertAndSend(RabbitProductUsedConfig.PRODUCT_USED_EXCHANGE, "", eventDto, message -> {
            message.getMessageProperties().setHeader("x-jwt-token", jwtTokenService.getToken());
            return message;
        });
    }
}