package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Balance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KardexRecordsDTOResponse {
    private Long idKardex;
    private ZonedDateTime date;
    private String detail;

    private Integer entryQuantity;
    private BigDecimal entryUnitPrice;
    private BigDecimal entryTotalPrice;

    private List<SaleDetailDTO> outputDetails;  
    private Integer outputQuantity;            
    private BigDecimal outputTotalPrice;   

    private List<Balance> balance;


    private int totalBalanceQuantity;
    private BigDecimal totalBalanceValue;
    
}
