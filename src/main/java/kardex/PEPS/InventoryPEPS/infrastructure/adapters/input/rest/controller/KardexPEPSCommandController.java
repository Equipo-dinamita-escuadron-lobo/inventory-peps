package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexCommandPort;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.AdjustmentEntryDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.AdjustmentExitDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.AdjustmentEntryDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.AdjustmentExitDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper.IKardexRestMapper;
import lombok.RequiredArgsConstructor;

/**
 * @brief REST controller for Kardex PEPS command operations
 * 
 * Handles HTTP requests for registering inventory adjustments (entries and exits)
 * and managing kardex records.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kardex/peps")
public class KardexPEPSCommandController {
    
    private final IKardexCommandPort kardexCommandPort;
    private final IKardexRestMapper kardexRestMapper;

    /**
     * @brief Registers a purchase adjustment (positive inventory adjustment)
     * 
     * @param adjustmentEntryDTORequest DTO containing adjustment details
     * @return Response with the registered adjustment information
     */
    @PostMapping("/purchase-adjustment")
    public ResponseEntity<ResponseDTO<AdjustmentEntryDTOResponse>> AdjustmentEntry(@Valid @RequestBody AdjustmentEntryDTORequest adjustmentEntryDTORequest) {
        Kardex response=kardexCommandPort.registerAdjustmentEntry(kardexRestMapper.toDomain(adjustmentEntryDTORequest));
        AdjustmentEntryDTOResponse adjustmentEntryDTOResponse=kardexRestMapper.toDTOResponse(response);
        ResponseDTO<AdjustmentEntryDTOResponse> responseDTO=ResponseDTO.<AdjustmentEntryDTOResponse>builder()
        .data(adjustmentEntryDTOResponse)
        .status(200)
        .message("kardex purchase registered sucesfully").build();
        return  responseDTO.of();
    }


    /**
     * @brief Registers a sale adjustment (negative inventory adjustment)
     * 
     * @param adjustmentExitDTORequest DTO containing adjustment details
     * @return Response with the registered adjustment information
     */
    @PostMapping("/sale-adjustment")
    public ResponseEntity<ResponseDTO<AdjustmentExitDTOResponse>> AdjustmentExit(@Valid @RequestBody AdjustmentExitDTORequest adjustmentExitDTORequest) {
        Kardex response=kardexCommandPort.registerAdjustmentExit(kardexRestMapper.toDomain(adjustmentExitDTORequest));
        AdjustmentExitDTOResponse adjustmentExitDTOResponse=kardexRestMapper.toDTOResponseSale(response);
        ResponseDTO<AdjustmentExitDTOResponse> responseDTO=ResponseDTO.<AdjustmentExitDTOResponse>builder()
        .data(adjustmentExitDTOResponse)
        .status(200)
        .message("kardex sale registered sucesfullly").build();
        
        return responseDTO.of();
    }

    /**
     * @brief Deletes all kardex records
     * @return Response confirming deletion
     */
    @DeleteMapping("/delete-all")
    public ResponseEntity<ResponseDTO<Void>> deleteAllKardex() {
        kardexCommandPort.deleteAll();
        
        ResponseDTO<Void> responseDto = ResponseDTO.<Void>builder()
                .data(null)
                .status(200)
                .message("All kardex records deleted successfully")
                .build();
        return responseDto.of();
    }


}
