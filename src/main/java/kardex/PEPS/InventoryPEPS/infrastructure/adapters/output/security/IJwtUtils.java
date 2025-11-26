package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.security;

/**
 * @brief Interface for JWT utility operations
 * 
 * Defines methods to retrieve user identification and token information
 * from the security context.
 */
public interface IJwtUtils {
    
    /**
     * @brief Retrieves the user ID from the token
     * @return The user identifier (subject)
     */
    String getId();
    
    /**
     * @brief Retrieves the raw JWT token string
     * @return The JWT token value
     */
    String getToken();
}
