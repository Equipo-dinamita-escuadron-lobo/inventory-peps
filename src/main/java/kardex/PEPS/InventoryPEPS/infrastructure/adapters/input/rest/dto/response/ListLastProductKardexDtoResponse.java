package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response;

import java.math.BigDecimal;

import kardex.PEPS.InventoryPEPS.domain.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief DTO for the last kardex record of a product
 * 
 * Used to display the most recent status of a product's inventory.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ListLastProductKardexDtoResponse {
    private Long productId;

    private Long quantity;

    private String factCode;

    private BigDecimal unitPrice;

    private String details;

    private MovementType type;

    private Long balanceQuantity;

    private BigDecimal balanceUnitPrice;

    private BigDecimal totalBalance;
}
