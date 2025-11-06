package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KardexAvailableQuantityDTOResponse {
     private Long idKardex;
    private ZonedDateTime date;
    private String details;
    private int availableQuantity;
    private BigDecimal unitPrice;
    private String type;
    private Long factCode;
}
