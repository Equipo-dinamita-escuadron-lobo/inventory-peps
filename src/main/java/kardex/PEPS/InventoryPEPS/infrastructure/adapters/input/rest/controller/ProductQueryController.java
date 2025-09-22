package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kardex.PEPS.InventoryPEPS.domain.port.output.IProductQueryOutputPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.ProductDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper.IProductResponseMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kardex/peps")
public class ProductQueryController {
    private final IProductResponseMapper productResponseMapper;
    private final IProductQueryOutputPort productQueryOutputPort;

    @GetMapping("/products/{enterpriseId}")
    public ResponseDTO<List<ProductDTOResponse>> getAllProductsByEnterprise(@PathVariable String enterpriseId) {
        List<ProductDTOResponse> productDtoResponses = productQueryOutputPort.findAll(enterpriseId)
                        .stream().map(productResponseMapper::toDtoResponse).toList();
        
        return ResponseDTO.<List<ProductDTOResponse>>builder()
                .data(productDtoResponses)
                .status(200)
                .message("Products retrieved successfully").build();
    }
    
}
