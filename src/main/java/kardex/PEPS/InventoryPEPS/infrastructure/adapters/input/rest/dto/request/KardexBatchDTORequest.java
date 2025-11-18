package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kardex.PEPS.InventoryPEPS.domain.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class KardexBatchDTORequest {
    @NotNull(message = "The field 'details' cannot be null")
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

     
    @NotNull(message = "Type cannot be null")
    private MovementType type;
    
}
