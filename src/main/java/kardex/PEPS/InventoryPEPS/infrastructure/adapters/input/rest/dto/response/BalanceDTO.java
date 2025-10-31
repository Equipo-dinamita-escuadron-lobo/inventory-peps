package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDTO {
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
