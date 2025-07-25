package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockDTOResponse {
    
    private Long id;

    private Long idProduct;

    private int amount;

    private Double price;


    private boolean status;

}
