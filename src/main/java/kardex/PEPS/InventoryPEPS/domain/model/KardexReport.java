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
public class KardexReport {
    private Long idKardex;
    private ZonedDateTime date;
    private String detail;

    private Integer entryQuantity;
    private BigDecimal entryUnitPrice;
    private BigDecimal entryTotalPrice;

    private Integer outputQuantity;
    private BigDecimal outputUnitPrice;
    private BigDecimal outputTotalPrice;

    private List<Balance> balance;


    private int totalBalanceQuantity;
    private BigDecimal totalBalanceValue;

}
