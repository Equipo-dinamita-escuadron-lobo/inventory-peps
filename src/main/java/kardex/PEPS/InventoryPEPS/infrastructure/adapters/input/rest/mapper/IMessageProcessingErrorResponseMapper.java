package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper;

import org.mapstruct.Mapper;

import kardex.PEPS.InventoryPEPS.domain.model.MessageProcessingError;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.MessageProcessingErrorDtoResponse;

/**
 * @brief MapStruct mapper for MessageProcessingError REST responses
 * 
 * Handles conversion between domain model and REST response DTOs
 * for message processing error operations.
 */
@Mapper(componentModel = "spring")
public interface IMessageProcessingErrorResponseMapper {
    

     /**
     * @brief Converts domain model to response DTO
     * @param messageProcessingError Domain model
     * @return Response DTO
     */


    MessageProcessingErrorDtoResponse toDtoResponse(MessageProcessingError messageProcessingError);
}
