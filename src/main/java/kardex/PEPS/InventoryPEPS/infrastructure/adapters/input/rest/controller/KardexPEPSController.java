package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexCommandPort;
import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexQueryPort;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexPurchaseDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexPurchaseDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper.IKardexRestMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kardex/peps")
public class KardexPEPSController {

    private final IKardexCommandPort kardexCommandPort;
   // private final IKardexQueryPort kardexQueryPort;

    private final IKardexRestMapper kardexRestMapper;


    @PostMapping("/purchase")
    public ResponseEntity<ResponseDTO<KardexPurchaseDTOResponse>> purchase(@RequestBody KardexPurchaseDTORequest kardexPurchaseDTORequest) {
        Kardex response=kardexCommandPort.registerPurchase(kardexRestMapper.toDomain(kardexPurchaseDTORequest));
        KardexPurchaseDTOResponse kardexPurchaseDTOResponse=kardexRestMapper.toDTOResponse(response);
        ResponseDTO<KardexPurchaseDTOResponse> responseDTO=ResponseDTO.<KardexPurchaseDTOResponse>builder()
        .data(kardexPurchaseDTOResponse)
        .status(200)
        .message("kardex purchase registered sucesfully").build();
        return  responseDTO.of();
    }
    
}
