package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kardex.PEPS.InventoryPEPS.application.ports.input.IProductSyncCommandPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * @brief REST controller for Product synchronization
 * 
 * Handles HTTP requests to trigger manual synchronization of products
 * from external sources.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kardex/peps/sync")
public class ProductSyncController {
    private final IProductSyncCommandPort productCommandPort;

    /**
     * @brief Triggers product synchronization for an enterprise
     * 
     * @param enterpriseId The enterprise identifier
     * @return Response indicating synchronization status
     */
    @GetMapping("/products/{enterpriseId}")
    public ResponseEntity<ResponseDTO<String>> syncProducts(@PathVariable String enterpriseId) {
        String result = productCommandPort.syncProductsByEnterpriseId(enterpriseId);
        ResponseDTO<String> responseDto = ResponseDTO.<String>builder()
                .data(result)
                .status(200)
                .message("Synchronization completed").build();
        return ResponseEntity.ok(responseDto);
    }
    
}
