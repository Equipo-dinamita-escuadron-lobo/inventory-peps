package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.MessageProcessingErrorEntity;

public interface IMessageProcessingErrorRepository extends JpaRepository<MessageProcessingErrorEntity, Long> {
    
}
