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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Balance {
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

  
    public static Balance fromMovement(int quantity, BigDecimal unitPrice) {
        validateQuantity(quantity);
        validateUnitPrice(unitPrice);
        
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
        return Balance.builder()
            .quantity(quantity)
            .unitPrice(unitPrice)
            .totalPrice(total)
            .build();
    }
    
    // Crear copia de balance
    public static Balance copy(Balance original) {
        if (original == null) {
            return null;
        }
        return new Balance(original.quantity, original.unitPrice, original.totalPrice);
    }
    
    // Reducir cantidad (para consumo FIFO)
    public void reduceQuantity(int amountToReduce) {
        if (amountToReduce <= 0) {
            throw new IllegalArgumentException("Amount to reduce must be positive");
        }
        if (this.quantity < amountToReduce) {
            throw new IllegalArgumentException(
                String.format("Cannot reduce %d from balance with only %d available", 
                    amountToReduce, this.quantity)
            );
        }
        
        this.quantity -= amountToReduce;
        this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
    
    //Verificar si puede satisfacer demanda
    public boolean canSatisfyDemand(int demandQuantity) {
        return this.quantity >= demandQuantity;
    }
    
    //Verificar si está agotado
    public boolean isEmpty() {
        return this.quantity <= 0;
    }
    
    // Obtener precio para cantidad específica
    public BigDecimal getPriceForQuantity(int qty) {
        if (qty > this.quantity) {
            throw new IllegalArgumentException("Requested quantity exceeds available quantity");
        }
        return this.unitPrice.multiply(BigDecimal.valueOf(qty));
    }
    
    //Recalcular total
    public void recalculateTotal() {
        this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
    
    // Verificar si coincide con precio unitario
    public boolean matchesUnitPrice(BigDecimal price) {
        return this.unitPrice.compareTo(price) == 0;
    }
    
    // Validaciones privadas
    private static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
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
        Balance balance = (Balance) o;
        return quantity == balance.quantity &&
               Objects.equals(unitPrice, balance.unitPrice) &&
               Objects.equals(totalPrice, balance.totalPrice);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(quantity, unitPrice, totalPrice);
    }
    
    @Override
    public String toString() {
        return String.format("Balance{quantity=%d, unitPrice=%s, totalPrice=%s}", 
            quantity, unitPrice, totalPrice);
    }

    
}
