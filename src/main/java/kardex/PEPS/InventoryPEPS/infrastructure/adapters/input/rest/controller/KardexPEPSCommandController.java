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
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexPurchaseDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexSaleDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexPurchaseDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexSaleDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper.IKardexRestMapper;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kardex/peps")
public class KardexPEPSCommandController {
    
    private final IKardexCommandPort kardexCommandPort;
    private final IKardexRestMapper kardexRestMapper;

    @PostMapping("/purchase-adjustment")
    public ResponseEntity<ResponseDTO<KardexPurchaseDTOResponse>> purchase(@Valid @RequestBody KardexPurchaseDTORequest kardexPurchaseDTORequest) {
        Kardex response=kardexCommandPort.registerPurchase(kardexRestMapper.toDomain(kardexPurchaseDTORequest));
        KardexPurchaseDTOResponse kardexPurchaseDTOResponse=kardexRestMapper.toDTOResponse(response);
        ResponseDTO<KardexPurchaseDTOResponse> responseDTO=ResponseDTO.<KardexPurchaseDTOResponse>builder()
        .data(kardexPurchaseDTOResponse)
        .status(200)
        .message("kardex purchase registered sucesfully").build();
        return  responseDTO.of();
    }


    @PostMapping("/sale-adjustment")
    public ResponseEntity<ResponseDTO<KardexSaleDTOResponse>> sale(@Valid @RequestBody KardexSaleDTORequest kardexSaleDTORequest) {
        Kardex response=kardexCommandPort.registerSale(kardexRestMapper.toDomain(kardexSaleDTORequest));
        KardexSaleDTOResponse kardexSaleDTOResponse=kardexRestMapper.toDTOResponseSale(response);
        ResponseDTO<KardexSaleDTOResponse> responseDTO=ResponseDTO.<KardexSaleDTOResponse>builder()
        .data(kardexSaleDTOResponse)
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
