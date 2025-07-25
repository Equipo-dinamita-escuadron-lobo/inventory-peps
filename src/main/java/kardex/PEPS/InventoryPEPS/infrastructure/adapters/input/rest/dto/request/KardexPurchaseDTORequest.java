package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KardexPurchaseDTORequest {
    private String details;
    private int amount;
    private BigDecimal unitPrice;
    private Long idProduct;

}
