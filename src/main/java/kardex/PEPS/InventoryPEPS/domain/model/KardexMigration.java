package kardex.PEPS.InventoryPEPS.domain.model;

import java.math.BigDecimal;
import kardex.PEPS.InventoryPEPS.domain.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class KardexMigration {
    
    private Long productId;

    private Long quantity;

    private String factCode;

    private BigDecimal unitPrice;

    private String details;

    private MovementType type;

    private Long balanceQuantity;

    private BigDecimal balanceUnitPrice;

    private BigDecimal totalBalance;


    /**
     * Valida que los datos del resumen sean consistentes.
     */
    public void validate() {
        if (productId == null) {
            throw new IllegalStateException("Product ID cannot be null");
        }
        if (quantity != null && quantity < 0) {
            throw new IllegalStateException("Quantity cannot be negative");
        }
        if (balanceQuantity != null && balanceQuantity < 0) {
            throw new IllegalStateException("Balance quantity cannot be negative");
        }
        if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Unit price cannot be negative");
        }
    }

    /**
     * Indica si hay stock disponible en el balance.
     */
    public boolean hasBalanceStock() {
        return balanceQuantity != null && balanceQuantity > 0;
    }

    /**
     * Calcula el valor total del movimiento original.
     */
    public BigDecimal calculateMovementValue() {
        if (quantity == null || unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }


}
