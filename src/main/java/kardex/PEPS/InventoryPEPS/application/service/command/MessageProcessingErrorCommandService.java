package kardex.PEPS.InventoryPEPS.application.service.command;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kardex.PEPS.InventoryPEPS.application.ports.input.IMessageProcessingErrorCommandPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IMessageProcessingErrorCommandOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IMessageProcessingErrorQueryOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IMessageServicePort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.i18n.MessageKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Service implementation for MessageProcessingError command operations
 * 
 * Handles message processing error deletion operations with validation
 * and transaction management.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageProcessingErrorCommandService implements IMessageProcessingErrorCommandPort {
    
     private final IMessageProcessingErrorCommandOutputPort messageProcessingErrorCommandOutputPort;
    private final IMessageProcessingErrorQueryOutputPort messageProcessingErrorQueryOutPutPort;
    private final IFormatterResultOutputPort formatterResultOutputPort;
    private final IMessageServicePort messageService;

    /**
     * @brief Deletes all message processing error records
     */
    @Override
    public void deleteAll() {
        log.info("Deleting all message processing errors");
        messageProcessingErrorCommandOutputPort.deleteAll();
        log.info("All message processing errors deleted successfully");
    }

    /**
     * @brief Deletes a specific message processing error by ID
     * @param id Error record identifier
     */
    @Override
    public void deleteById(Long id) {
        log.info("Deleting message processing error by id: {}", id);
        
        // Validate existence
        if (!messageProcessingErrorQueryOutPutPort.findById(id).isPresent()) {
            formatterResultOutputPort.returnEntityDoesNotExistErrorResponse(404, 
                messageService.getMessage(MessageKeys.ERROR_NOT_FOUND, "Message processing error with id: " + id));
        }
        
        messageProcessingErrorCommandOutputPort.deleteById(id);
        log.info("Message processing error with id {} deleted successfully", id);
    }

}
