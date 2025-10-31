package kardex.PEPS.InventoryPEPS.domain.port.output;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kardex.PEPS.InventoryPEPS.domain.model.MessageProcessingError;

public interface IMessageProcessingErrorQueryOutputPort {

     /**
     * @brief Finds a message processing error by ID
     * @param id Error record identifier
     * @return Optional containing the error record if found
     */
    Optional<MessageProcessingError> findById(Long id);
    
    /**
     * @brief Finds the most recent message processing error
     * @return Optional containing the latest error record if found
     */
    Optional<MessageProcessingError> findLastRecord();
    
    /**
     * @brief Retrieves all message processing errors with pagination
     * @param pageable Pagination parameters
     * @return Paginated error records
     */
    Page<MessageProcessingError> findAll(Pageable pageable);
}
