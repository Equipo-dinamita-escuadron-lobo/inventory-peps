package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexCommandPort;
import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexQueryPort;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.KardexReport;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexByDateDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexPurchaseDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexPurchaseReturnDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexSaleDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexSaleReturnDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexPurchaseDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexRecordsDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexSaleDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper.IKardexRestMapper;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;




@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kardex/peps")
public class KardexPEPSController {

    private final IKardexCommandPort kardexCommandPort;
    private final IKardexQueryPort kardexQueryPort;

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
    @PostMapping("/sale")
    public ResponseEntity<ResponseDTO<KardexSaleDTOResponse>> sale(@RequestBody KardexSaleDTORequest kardexSaleDTORequest) {
        Kardex response=kardexCommandPort.registerSale(kardexRestMapper.toDomain(kardexSaleDTORequest));
        KardexSaleDTOResponse kardexSaleDTOResponse=kardexRestMapper.toDTOResponseSale(response);
        ResponseDTO<KardexSaleDTOResponse> responseDTO=ResponseDTO.<KardexSaleDTOResponse>builder()
        .data(kardexSaleDTOResponse)
        .status(200)
        .message("kardex sale registered sucesfullly").build();
        
        return responseDTO.of();
    }
    @GetMapping("/kardexlist")
    public ResponseDTO<Page<KardexRecordsDTOResponse>>getKardexListPEPSByDate(
        @RequestParam @NotNull(message = "The product ID is required.") Long productId,
        @Valid @ModelAttribute KardexByDateDTORequest kardex,Pageable pageable) {

        // 1. Llama al servicio (esto ya es correcto)
        Page<KardexReport> kardexReports = kardexQueryPort.getRecordsKardexByProduct(productId, kardex.getStartDate(), kardex.getEndDate(), pageable);
        
        // 2. Mapea la página usando el método correcto (ahora se llama toDTORecord)
        // MapStruct aplicará toDTORecord a cada elemento de la página.
        Page<KardexRecordsDTOResponse> kardexRecords = kardexReports.map(kardexRestMapper::toDTORecord);

        // 3. Construye la respuesta (esto ya es correcto)
        return  ResponseDTO.<Page<KardexRecordsDTOResponse>>builder() //response = ResponseDTO.<Page<KardexRecordsDTOResponse>>builder()
                .data(kardexRecords)
                .status(200)
                .message("Kardex records retrieved successfully")
                .build();
        
        // 4. Devuelve un ResponseEntity con el código de estado OK.
        //return ResponseEntity.ok(response);
    }
    @PostMapping("/purchasereturn")
    public ResponseEntity<ResponseDTO<KardexPurchaseDTOResponse>> purchaseReturn(@RequestBody KardexPurchaseReturnDTORequest kardexPurchaseReturnDTORequest) {
        Kardex response=kardexCommandPort.registerPurchaseReturn(kardexRestMapper.toDomain(kardexPurchaseReturnDTORequest));
        KardexPurchaseDTOResponse kardexPurchaseDTOResponse=kardexRestMapper.toDTOResponse(response);
         ResponseDTO<KardexPurchaseDTOResponse> responseDTO=ResponseDTO.<KardexPurchaseDTOResponse>builder()
        .data(kardexPurchaseDTOResponse)
        .status(200)
        .message("kardex purchase return registered sucesfully").build();
        return  responseDTO.of();
    }
    @PostMapping("/salereturn")
    public ResponseEntity<ResponseDTO<List<KardexPurchaseDTOResponse>>> saleReturn(@RequestBody KardexSaleReturnDTORequest kardexSaleReturnDTORequest) {
        List<Kardex> salesReturn=kardexCommandPort.registerSaleReturn(kardexRestMapper.toDomain(kardexSaleReturnDTORequest));
        List<KardexPurchaseDTOResponse> sales=kardexRestMapper.toDTOSaleReturn(salesReturn);
        ResponseDTO<List<KardexPurchaseDTOResponse>> responseDTO=ResponseDTO.<List<KardexPurchaseDTOResponse>>builder()
        .data(sales)
        .status(200)
        .message("Kardex sale return registered sucesfully").build();

        return responseDTO.of();
    }
    
    

    

    
    
    
}
