package kardex.PEPS.InventoryPEPS.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class InventoryQueue {
   private final LinkedList<Balance> balances;
    

    public InventoryQueue() {
        this.balances = new LinkedList<>();
    }
    
    // Agregar balance al final (FIFO)
    public void addBalance(Balance balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        if (balance.isEmpty()) {
            throw new IllegalArgumentException("Cannot add empty balance");
        }
        this.balances.add(balance);
    }
    
    // Agregar al inicio (para devoluciones)
    public void addBalanceFirst(Balance balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        this.balances.addFirst(balance);
    }
    
    //Consumir cantidad usando FIFO
    public List<SaleDetail> consumeQuantity(int quantityToConsume) {
        if (quantityToConsume <= 0) {
            throw new IllegalArgumentException("Quantity to consume must be positive");
        }
        
        if (!hasEnoughStock(quantityToConsume)) {
            throw new IllegalArgumentException(
                String.format("Insufficient stock. Required: %d, Available: %d", 
                    quantityToConsume, getTotalQuantity())
            );
        }
        
        return consumeQuantityInternal(quantityToConsume);
    }

    public List<SaleDetail> consumeQuantityForReport(int quantityToConsume) {
        if (quantityToConsume <= 0) {
            throw new IllegalArgumentException("Quantity to consume must be positive");
        }
        
       
        return consumeQuantityInternal(quantityToConsume);
    }
    
    //  compartido con la lógica real de consumo
    private List<SaleDetail> consumeQuantityInternal(int quantityToConsume) {
        List<SaleDetail> details = new ArrayList<>();
        int remaining = quantityToConsume;
        
        while (remaining > 0 && !balances.isEmpty()) {
            Balance currentLot = balances.peekFirst();
            
            int amountFromThisLot = Math.min(remaining, currentLot.getQuantity());
            
            // Crear detalle usando Factory Method
            SaleDetail detail = SaleDetail.fromBalance(currentLot, amountFromThisLot);
            details.add(detail);
            
            // Reducir cantidad del lote
            currentLot.reduceQuantity(amountFromThisLot);
            
            // Si el lote está vacío, eliminarlo
            if (currentLot.isEmpty()) {
                balances.pollFirst();
            }
            
            remaining -= amountFromThisLot;
        }
        
        return details;
    }

    
    //  Remover cantidad específica por precio
    public List<SaleDetail> removeByUnitPrice(int quantityToRemove, BigDecimal unitPrice) {
        if (quantityToRemove <= 0) {
            throw new IllegalArgumentException("Quantity to remove must be positive");
        }
        
        List<SaleDetail> details = new ArrayList<>();
        int remaining = quantityToRemove;
        
        Iterator<Balance> iterator = balances.iterator();
        
        while (iterator.hasNext() && remaining > 0) {
            Balance balance = iterator.next();
            
            if (balance.matchesUnitPrice(unitPrice)) {
                int amountToTake = Math.min(remaining, balance.getQuantity());
                
                SaleDetail detail = SaleDetail.fromBalance(balance, amountToTake);
                details.add(detail);
                
                balance.reduceQuantity(amountToTake);
                remaining -= amountToTake;
                
                if (balance.isEmpty()) {
                    iterator.remove();
                }
            }
        }
        
        return details;
    }
    
    // Verificar si hay suficiente stock
    public boolean hasEnoughStock(int requiredQuantity) {
        return getTotalQuantity() >= requiredQuantity;
    }
    
    //Obtener cantidad total
    public int getTotalQuantity() {
        return balances.stream()
            .mapToInt(Balance::getQuantity)
            .sum();
    }
    
    // Obtener valor total
    public BigDecimal getTotalValue() {
        return balances.stream()
            .map(Balance::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Verificar si está vacío
    public boolean isEmpty() {
        return balances.isEmpty();
    }
    
    //Obtener copia de balances
    public List<Balance> getBalancesCopy() {
        return balances.stream()
            .map(Balance::copy)
            .toList();
    }
    
    //Obtener balances inmutables
    public List<Balance> getBalancesReadOnly() {
        return Collections.unmodifiableList(new ArrayList<>(balances));
    }
    
    //Obtener primer balance
    public Optional<Balance> getFirstBalance() {
        return Optional.ofNullable(balances.peekFirst()); 
    }
    
    //Limpiar cola
    public void clear() {
        balances.clear();
    }
    
    //Obtener tamaño de la cola
    public int size() {
        return balances.size();
    }
    
    @Override
    public String toString() {
        return String.format("InventoryQueue{size=%d, totalQuantity=%d, totalValue=%s}", 
            balances.size(), getTotalQuantity(), getTotalValue());
    }
}
