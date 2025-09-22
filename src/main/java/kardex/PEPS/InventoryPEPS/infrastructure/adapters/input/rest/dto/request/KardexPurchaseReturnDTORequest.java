package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KardexPurchaseReturnDTORequest {
    
    private Long idKardex;

    private String details;
    @NotNull(message = "The field 'quantity' cannot be null")
    @Positive(message = "The quantity must be positive")
    private int quantity;
    private BigDecimal unitPrice;

    @NotNull(message = "The field 'factCode' cannot be null")
    @Positive(message = "The factCode must be positive")
    private Long factCode;
    @NotNull(message = "The field 'productId' cannot be null")
    @Positive(message = "The productId must be positive")
    private Long productId; 
}
