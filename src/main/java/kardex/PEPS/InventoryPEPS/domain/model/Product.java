package kardex.PEPS.InventoryPEPS.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    private Long idProduct;
    private String name;
    private String reference;
    private String presentation;
    private String manager;
    private List<Kardex>recordsKardex;

}
