package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="Product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idProduct;
    
    @Column(name ="name", nullable = false, length =50 )
    private String name;

    @Column(name ="reference", nullable = false, length =50 )
    private String reference;

    @Column(name ="presentation", nullable = false, length =50 )
    private String presentation;

    @Column(name ="responsible", nullable = false, length =50 )
    private String manager;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "objProduct")
    private List<KardexEntity> recordsKardex;

}
