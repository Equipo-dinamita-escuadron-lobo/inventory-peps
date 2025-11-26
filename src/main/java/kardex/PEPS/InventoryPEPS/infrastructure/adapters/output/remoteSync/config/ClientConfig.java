package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.aspect.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Configuration for external service clients
 * 
 * Configures WebClient proxies for communicating with external microservices
 * (Stock, Products, Config, KardexExternal). Handles base URLs and JWT propagation.
 */
@Configuration
@EnableConfigurationProperties(ClientProperties.class)
@RequiredArgsConstructor
@Slf4j
public class ClientConfig {
     
    private final JwtTokenService jwtTokenService;

    /**
     * @brief Generic factory method to create WebClient proxies
     * 
     * Centralizes the logic for building WebClient instances and HttpServiceProxyFactory.
     * Applies common configurations like base URL and JWT propagation filter.
     * 
     * @param <T> The type of the client interface
     * @param webClientBuilder The builder for WebClient
     * @param baseUrl The base URL for the service
     * @param clientInterface The interface class of the client
     * @return A proxy instance of the client interface
     * @throws IllegalArgumentException if baseUrl is null or empty
     */
    private <T> T createWebClientProxy(WebClient.Builder webClientBuilder, String baseUrl, Class<T> clientInterface) {
        // Validar que baseUrl no sea null o vacío
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("BaseURL cannot be null or empty for client: " + clientInterface.getSimpleName());
        }
        
        log.info("Creating WebClient proxy for {} with baseUrl: {}", clientInterface.getSimpleName(), baseUrl);
        
        // 1. Construye una instancia de WebClient específica para este cliente
        WebClient webClient = webClientBuilder
                .baseUrl(baseUrl)
                .filter(jwtPropagationFilter())
                .build();

        // 2. Crea el adaptador y la fábrica del proxy
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        // 3. Crea y devuelve el cliente
        return factory.createClient(clientInterface);
    }

    @Bean
    IStockClient stockClient(WebClient.Builder webClientBuilder, ClientProperties properties) {
        String baseUrl = properties.getStock().getBaseUrl();
        return createWebClientProxy(webClientBuilder, baseUrl, IStockClient.class);
    }

    @Bean
    IProductClient productClient(WebClient.Builder webClientBuilder, ClientProperties properties) {
        String baseUrl = properties.getProducts().getBaseUrl();
        return createWebClientProxy(webClientBuilder, baseUrl, IProductClient.class);
    }

      @Bean
    IConfigClient configClient(WebClient.Builder webClientBuilder, ClientProperties properties) {
        String baseUrl = properties.getConfig().getBaseUrl();
        return createWebClientProxy(webClientBuilder, baseUrl, IConfigClient.class);
    }

    @Bean
    IKardexExternalClient kardexExternalClient(WebClient.Builder webClientBuilder, ClientProperties properties) {
        String baseUrl = properties.getKardexExternal().getBaseUrl();
        return createWebClientProxy(webClientBuilder, baseUrl, IKardexExternalClient.class);
    }



    /**
     * @brief Filter to propagate JWT token in HTTP requests
     * 
     * Intercepts outgoing requests to inject the "Authorization" header with the Bearer token.
     * Retrieves the token from the current context (HTTP or RabbitMQ).
     * 
     * @return ExchangeFilterFunction that applies the JWT header
     */
    private ExchangeFilterFunction jwtPropagationFilter() {
        return (clientRequest, next) -> {
            try {
                String tokenValue = jwtTokenService.getToken();
                
                // Remover prefijo "Bearer " si ya existe en el token
                final String finalTokenValue = tokenValue.startsWith("Bearer ") 
                    ? tokenValue.substring(7) 
                    : tokenValue;
                
                ClientRequest newRequest = ClientRequest.from(clientRequest)
                        .headers(headers -> headers.setBearerAuth(finalTokenValue))
                        .build();

                return next.exchange(newRequest);
                
            } catch (Exception e) {
                log.error("Error al obtener token JWT para propagación: {}", e.getMessage());
                throw new IllegalStateException("No JWT token available for propagation", e);
            }
        };
    }


}
