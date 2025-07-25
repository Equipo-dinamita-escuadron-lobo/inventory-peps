package kardex.PEPS.InventoryPEPS.domain.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Kardex {
    private Long idKardex;
    private ZonedDateTime date;
    private String details;
    private int amount;
    private BigDecimal unitPrice;
    private MovementType type;
    private Product objProduct;

    private List<DetailOutput>detailsOutput;
    private List<DetailOutput>detailsOrigin;

    
}
