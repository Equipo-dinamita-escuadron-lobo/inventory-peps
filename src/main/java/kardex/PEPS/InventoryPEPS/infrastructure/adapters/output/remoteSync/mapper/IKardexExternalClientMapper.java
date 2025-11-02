package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexBatchDTORequest;

/**
 * @brief Mapper for converting external kardex DTOs to domain models
 */
@Mapper(componentModel = "spring")
public interface IKardexExternalClientMapper {
    
     /**
     * @brief Converts external DTO to domain model
     * @param dto External kardex DTO
     * @return Domain kardex model
     */
    @Mapping(target = "idKardex", ignore = true)
    @Mapping(target = "date", ignore = true)
    Kardex toDomain(KardexBatchDTORequest dto);
}
