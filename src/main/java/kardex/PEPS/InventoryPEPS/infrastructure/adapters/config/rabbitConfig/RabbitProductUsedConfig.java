package kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.rabbitConfig;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;

/**
 * @brief Configuration for Product Used events RabbitMQ messaging
 * 
 * Defines the exchanges, queues, and bindings required for
 * publishing and consuming events when a product is used.
 */

@Configuration
public class RabbitProductUsedConfig {
    public static final String PRODUCT_USED_EXCHANGE = "product.used.exchange";
    public static final String PRODUCT_USED_QUEUE = "product.used.queue";

    /**
     * @brief Creates the fanout exchange for product used events
     * @return Configured FanoutExchange instance
     */
    @Bean
    FanoutExchange productUsedExchange() {
        return new FanoutExchange(PRODUCT_USED_EXCHANGE, true, false);
    }

    /**
     * @brief Creates the durable queue for product used events
     * @return Configured durable Queue instance
     */
    @Bean
    Queue productUsedQueue() {
        return QueueBuilder.durable(PRODUCT_USED_QUEUE).build();
    }

    /**
     * @brief Binds the product used queue to the product used exchange
     * @return Binding configuration between queue and exchange
     */
    @Bean
    Binding productUsedQueueBinding() {
        return BindingBuilder.bind(productUsedQueue()).to(productUsedExchange());
    }
    
}
