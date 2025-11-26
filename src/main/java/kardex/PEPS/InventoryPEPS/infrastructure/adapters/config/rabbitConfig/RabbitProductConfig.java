package kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.rabbitConfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Configuration for Product-related RabbitMQ messaging
 * 
 * Defines the exchanges, queues, and bindings required for
 * integrating product events into the Kardex system.
 */
@Configuration
@Slf4j
@Profile("!test") 
public class RabbitProductConfig {
    public static final String PRODUCT_EXCHANGE = "product.exchange";
    public static final String PRODUCT_KARDEX_QUEUE = "product.peps.queue";

    /**
     * @brief Creates the durable queue for product-kardex integration
     * @return Configured durable Queue instance
     */
    @Bean
    Queue productKardexQueue() {
        return QueueBuilder.durable(PRODUCT_KARDEX_QUEUE).build();
    }

    /**
     * @brief Creates the fanout exchange for product events
     * @return Configured FanoutExchange instance
     */
    @Bean
    FanoutExchange productExchange() {
        return new FanoutExchange(PRODUCT_EXCHANGE, true, false);
    }

    /**
     * @brief Binds the product-kardex queue to the product exchange
     * @return Binding configuration between queue and exchange
     */
    @Bean
    Binding productKardexQueueBinding() {
        return BindingBuilder.bind(productKardexQueue()).to(productExchange());
    }
    
}
