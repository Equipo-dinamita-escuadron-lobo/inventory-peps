package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kardex.PEPS.InventoryPEPS.domain.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "Kardex")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KardexEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKardex;

    @Column(name = "date", nullable=false)
    private  ZonedDateTime date;

    @Column(name="detail", nullable = false)
    private String details;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name="unit_value")
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type")
    private MovementType type;

    @ManyToOne
    @JoinColumn(name="idProduct", nullable = false)
    private ProductEntity objProduct;

   @OneToMany(mappedBy = "movementSale")
    private List<DetailOutputEntity> detailsOutput;

    @OneToMany(mappedBy = "movementOrigin")
    private List<DetailOutputEntity> detailsOrigin;
    
}
