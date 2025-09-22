package kardex.PEPS.InventoryPEPS.domain.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
    private Long factCode;
    private ZonedDateTime date;
    private String details;
    private int quantity;
    private BigDecimal unitPrice;
    private MovementType type;
    private int availableQuantity;

    private Product product;
    private List<DetailOutput>detailsOutput= new ArrayList<>();
    private List<DetailOutput>detailsOrigin= new ArrayList<>(); 

    
}
