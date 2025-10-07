package kardex.PEPS.InventoryPEPS.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleDetail {
    private int quantityUsed;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
    //Crear detalle desde consumo de lote
    public static SaleDetail fromLotConsumption(int quantityUsed, BigDecimal unitPrice) {
        validateQuantityUsed(quantityUsed);
        validateUnitPrice(unitPrice);
        
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantityUsed));
        
        return SaleDetail.builder()
            .quantityUsed(quantityUsed)
            .unitPrice(unitPrice)
            .totalPrice(total)
            .build();
    }
    
    //Crear desde Balance
    public static SaleDetail fromBalance(Balance balance, int quantityConsumed) {
        if (!balance.canSatisfyDemand(quantityConsumed)) {
            throw new IllegalArgumentException("Balance cannot satisfy demand");
        }
        return fromLotConsumption(quantityConsumed, balance.getUnitPrice());
    }
    
    // Calcular costo promedio
    public BigDecimal getAverageUnitPrice() {
        if (quantityUsed <= 0) {
            return BigDecimal.ZERO;
        }
        return totalPrice.divide(BigDecimal.valueOf(quantityUsed), 2, java.math.RoundingMode.HALF_UP);
    }
    
    //Recalcular total
    public void recalculateTotal() {
        this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantityUsed));
    }
    
    //Verificar si es válido
    public boolean isValid() {
        return quantityUsed > 0 && 
               unitPrice != null && 
               unitPrice.compareTo(BigDecimal.ZERO) >= 0 &&
               totalPrice != null;
    }
    
    // Validaciones privadas
    private static void validateQuantityUsed(int quantityUsed) {
        if (quantityUsed <= 0) {
            throw new IllegalArgumentException("Quantity used must be positive");
        }
    }
    
    private static void validateUnitPrice(BigDecimal unitPrice) {
        Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleDetail that = (SaleDetail) o;
        return quantityUsed == that.quantityUsed &&
               Objects.equals(unitPrice, that.unitPrice) &&
               Objects.equals(totalPrice, that.totalPrice);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(quantityUsed, unitPrice, totalPrice);
    }
    
    @Override
    public String toString() {
        return String.format("SaleDetail{quantityUsed=%d, unitPrice=%s, totalPrice=%s}", 
            quantityUsed, unitPrice, totalPrice);
    }

}
