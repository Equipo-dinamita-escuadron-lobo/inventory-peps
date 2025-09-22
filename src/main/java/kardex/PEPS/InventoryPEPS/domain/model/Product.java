package kardex.PEPS.InventoryPEPS.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private Long id;
    private Long productId;
    private String name;
    private String reference;
    private String presentation;
    private String manager;
    private String enterpriseId;
    private boolean state;
    
    private List<Kardex>recordsKardex;

}
