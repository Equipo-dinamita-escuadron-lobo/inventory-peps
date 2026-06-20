package kardex.PEPS.InventoryPEPS.domain.port.output;

/**
 * @brief Output port for message retrieval service
 * 
 * Provides methods to retrieve localized or parameterized messages
 * for user feedback and error reporting.
 */
public interface  IMessageServicePort {
    /**
     * @brief Retrieves a message by its key
     * @param key Message key
     * @param args Arguments for message formatting
     * @return Formatted message string
     */
     public String getMessage(String key, Object... args);

    /**
     * @brief Retrieves a message by its key with a default fallback
     * @param key Message key
     * @param defaultMessage Default message if key is not found
     * @param args Arguments for message formatting
     * @return Formatted message string
     */
    public String getMessage(String key, String defaultMessage, Object... args);
}
