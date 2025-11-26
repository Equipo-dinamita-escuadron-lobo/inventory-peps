package kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import kardex.PEPS.InventoryPEPS.domain.port.output.IMessageServicePort;
import lombok.RequiredArgsConstructor;

/**
 * @brief Service for managing internationalized messages
 * 
 * Implementation of the output port to retrieve localized messages
 * using Spring's MessageSource.
 */
@Service
@RequiredArgsConstructor
public class MessageService implements IMessageServicePort{

    private final MessageSource messageSource;


    /**
     * @brief Retrieves a message using the current locale
     * 
     * @param key The message key
     * @param args Arguments to format the message
     * @return The formatted message
     */
    @Override
    public String getMessage(String key, Object... args) {
       return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    /**
     * @brief Retrieves a message with a default value if key is not found
     * 
     * @param key The message key
     * @param defaultMessage The default message to return if key is missing
     * @param args Arguments to format the message
     * @return The formatted message or the default message
     */
    @Override
    public String getMessage(String key, String defaultMessage, Object... args) {
       return messageSource.getMessage(key, args, defaultMessage, LocaleContextHolder.getLocale());  
    }
    
}
