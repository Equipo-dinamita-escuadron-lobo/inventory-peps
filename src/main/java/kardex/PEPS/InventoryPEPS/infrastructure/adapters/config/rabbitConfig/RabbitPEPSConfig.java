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
 * @brief Configuration for PEPS (FIFO) specific RabbitMQ messaging
 * 
 * Defines the exchanges, queues, and bindings required for
 * PEPS inventory movement events.
 */
@Configuration
@Slf4j
@Profile("!test")
public class RabbitPEPSConfig {
    public static final String PEPS_EXCHANGE = "peps.exchange";
    public static final String PEPS_QUEUE = "peps.queue";

   
  
   /**
    * @brief Creates the durable queue for PEPS operations
    * @return Configured durable Queue instance
    */
   @Bean
    Queue pepsQueue() {
        return QueueBuilder.durable(PEPS_QUEUE).build();
    }


    /**
     * @brief Creates the fanout exchange for PEPS events
     * @return Configured FanoutExchange instance
     */
    @Bean
    FanoutExchange pepsExchange() {
        return new FanoutExchange(PEPS_EXCHANGE, true, false);
    }


    /**
     * @brief Binds the PEPS queue to the PEPS exchange
     * @return Binding configuration between queue and exchange
     */
    @Bean
    Binding pepsBinding() {
        return BindingBuilder.bind(pepsQueue()).to(pepsExchange());
    }

    

}
