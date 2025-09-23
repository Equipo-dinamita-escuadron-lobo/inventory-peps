package kardex.PEPS.InventoryPEPS.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleDetail {
    private int amountUsed;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
}
