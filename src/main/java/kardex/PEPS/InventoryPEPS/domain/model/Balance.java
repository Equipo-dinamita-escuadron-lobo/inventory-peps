package kardex.PEPS.InventoryPEPS.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Balance {
    private int amount;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
