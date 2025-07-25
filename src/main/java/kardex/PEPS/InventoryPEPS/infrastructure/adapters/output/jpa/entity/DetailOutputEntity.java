package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DetailOutputFIFO")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DetailOutputEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetailOutput;

    @Column(name = "amountUsed", nullable = false)
    private int amountUsed;

    @Column(name="unit_value", nullable = false)
    private BigDecimal UnitPrice;

   @ManyToOne
    @JoinColumn(name = "id_movement_sale")
    private KardexEntity movementSale;

    @ManyToOne
    @JoinColumn(name = "id_movement_origin")
    private KardexEntity movementOrigin;

}
