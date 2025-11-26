package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.aspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.security.IJwtUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Unified service for managing JWT tokens from both HTTP and RabbitMQ contexts
 * 
 * Provides seamless token access across different execution contexts,
 * handling both web requests and message processing scenarios.
 */
@Service
@Slf4j
public class JwtTokenService {
    @Autowired
    private IJwtUtils jwtUtils;

    // ThreadLocal para tokens de RabbitMQ
    private static final ThreadLocal<String> rabbitJwtToken = new ThreadLocal<>();
    private static final ThreadLocal<String> rabbitTenantId = new ThreadLocal<>();


    /**
     * @brief Sets the JWT token for the RabbitMQ context
     * 
     * Used when receiving a RabbitMQ message to store the token in ThreadLocal.
     * @param token The JWT token string
     */
    public void setRabbitJwtToken(String token) {
        rabbitJwtToken.set(token);
        log.debug("Token JWT establecido para contexto RabbitMQ");
    }

    /**
     * @brief Sets the tenant ID for the RabbitMQ context
     * 
     * Used when receiving a RabbitMQ message to store the tenant ID in ThreadLocal.
     * @param tenantId The tenant identifier
     */
    public void setRabbitTenantId(String tenantId) {
        rabbitTenantId.set(tenantId);
        log.debug("Tenant ID establecido para contexto RabbitMQ: {}", tenantId);
    }

    /**
     * @brief Retrieves the JWT token from the current context
     * 
     * Checks RabbitMQ context (ThreadLocal) first, then falls back to
     * standard HTTP SecurityContext.
     * 
     * @return The JWT token string
     * @throws IllegalStateException If no token is available in either context
     */
    public String getToken() {
        String token = rabbitJwtToken.get();
        if (token != null) {
            log.debug("Usando token JWT del contexto RabbitMQ");
            return token;
        }

        try {
            token = jwtUtils.getToken();
            log.debug("Usando token JWT del contexto HTTP");
            return token;
        } catch (Exception e) {
            log.warn("No se pudo obtener token del contexto HTTP: {}", e.getMessage());
            throw new IllegalStateException("No hay token JWT disponible ni en contexto RabbitMQ ni HTTP", e);
        }
    }

    /**
     * @brief Retrieves the tenant ID from the current context
     * 
     * Checks RabbitMQ context (ThreadLocal) first, then falls back to
     * standard HTTP SecurityContext.
     * 
     * @return The tenant identifier
     * @throws IllegalStateException If no tenant ID is available in either context
     */
    public String getTenantId() {
        String tenantId = rabbitTenantId.get();
        if (tenantId != null) {
            log.debug("Usando tenant ID del contexto RabbitMQ: {}", tenantId);
            return tenantId;
        }

        try {
            tenantId = jwtUtils.getId();
            log.debug("Usando tenant ID del contexto HTTP: {}", tenantId);
            return tenantId;
        } catch (Exception e) {
            log.warn("No se pudo obtener tenant ID del contexto HTTP: {}", e.getMessage());
            throw new IllegalStateException("No hay tenant ID disponible ni en contexto RabbitMQ ni HTTP", e);
        }
    }


    /**
     * @brief Clears the RabbitMQ context for the current thread
     * 
     * Removes token and tenant ID from ThreadLocal storage.
     * Should be called in the finally block of the RabbitMQ aspect.
     */
    public void clearRabbitContext() {
        rabbitJwtToken.remove();
        rabbitTenantId.remove();
        log.debug("Contexto RabbitMQ limpiado");
    }

    /**
     * @brief Checks if currently executing within a RabbitMQ context
     * @return True if a RabbitMQ token is present, false otherwise
     */
    public boolean isInRabbitContext() {
        return rabbitJwtToken.get() != null;
    }

}
