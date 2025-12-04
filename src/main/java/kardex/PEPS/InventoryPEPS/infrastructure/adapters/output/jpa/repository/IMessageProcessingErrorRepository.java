package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.MessageProcessingErrorEntity;


/**
 * @brief Repository for managing message processing errors
 * 
 * Handles database operations for tracking errors that occur during
 * asynchronous message processing.
 */
public interface IMessageProcessingErrorRepository extends JpaRepository<MessageProcessingErrorEntity, Long> {
      /**
     * @brief Finds the most recent message processing error ordered by error timestamp
     * @return Optional containing the latest error record if found
     */
    Optional<MessageProcessingErrorEntity> findFirstByOrderByErrorTimestampDesc();
}
