package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexQueryPort;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.KardexMigration;
import kardex.PEPS.InventoryPEPS.domain.model.KardexReport;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexByDateDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexAvailableQuantityDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexPurchaseDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexRecordsDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.ListLastProductKardexDtoResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper.IKardexRestMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kardex/peps")
public class KardexPEPSQueryController {

    private final IKardexQueryPort kardexQueryPort;
    private final IKardexRestMapper kardexRestMapper;


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

    @GetMapping("/kardex-available-quantity/{productId}")
    public ResponseEntity<ResponseDTO<List<KardexAvailableQuantityDTOResponse>>> getAvailable(@PathVariable Long productId) {

        List<Kardex> listResponse=kardexQueryPort.getKardexAvailableQuantityByProduct(productId);


        List<KardexAvailableQuantityDTOResponse> listKardexPurchaseDTOResponse=kardexRestMapper.toKardexDTO(listResponse);
        ResponseDTO<List<KardexAvailableQuantityDTOResponse>> listResponseDTO=ResponseDTO.<List<KardexAvailableQuantityDTOResponse>>builder()
        .data(listKardexPurchaseDTOResponse)
        .status(200)
        .message("kardex purchase registered sucesfully").build();
        return  listResponseDTO.of();


    }
    

     /**
     * @brief Retrieves the last kardex record for all products of an enterprise
     * @param enterpriseId Enterprise identifier to filter products
     * @return Response with list of last kardex records for each product
     */
    @GetMapping("/last-kardex-peps-all-products")
    public ResponseDTO<List<ListLastProductKardexDtoResponse>> getLastKardexForAllProducts(
        @RequestParam @NotNull(message = "The enterprise ID is required.") String enterpriseId) {
        List<KardexMigration> KardexMigrations=kardexQueryPort.findLastKardexForAllProducts(enterpriseId);
        List<ListLastProductKardexDtoResponse> response=kardexRestMapper.toListLastProductKardexDtoResponseList(KardexMigrations);

        return ResponseDTO.<List<ListLastProductKardexDtoResponse>>builder()
                .data(response)
                .status(200)
                .message("Last kardex records for all products retrieved successfully")
                .build();

    }
    

   
    
}
