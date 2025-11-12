package kardex.PEPS.InventoryPEPS.domain.model;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import kardex.PEPS.InventoryPEPS.domain.enums.MovementType;
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


    
    public static Kardex createPurchase(Long factCode, String details, int quantity, 
                                       BigDecimal unitPrice, Product product) {
        validateProduct(product);
        validateQuantity(quantity);
        validateUnitPrice(unitPrice);
        validateFactCode(factCode);
        
        Kardex kardex = new Kardex();
        kardex.setFactCode(factCode);
        kardex.setDate(ZonedDateTime.now());
        kardex.setDetails(details);
        kardex.setQuantity(quantity);
        kardex.setUnitPrice(unitPrice);
        kardex.setType(MovementType.PURCHASE);
        kardex.setAvailableQuantity(quantity);
        kardex.setProduct(product);
        kardex.setDetailsOutput(new ArrayList<>());
        kardex.setDetailsOrigin(new ArrayList<>());
        
        return kardex;
    }

    public static Kardex createSale(Long factCode, String details, int quantity, 
                                   BigDecimal unitPrice, Product product) {
        validateProduct(product);
        validateQuantity(quantity);
        validateFactCode(factCode);
        
        Kardex kardex = new Kardex();
        kardex.setFactCode(factCode);
        kardex.setDate(ZonedDateTime.now());
        kardex.setDetails(details);
        kardex.setQuantity(quantity);
        kardex.setUnitPrice(unitPrice);
        kardex.setType(MovementType.SALE);
        kardex.setAvailableQuantity(0);
        kardex.setProduct(product);
        kardex.setDetailsOutput(new ArrayList<>());
        kardex.setDetailsOrigin(new ArrayList<>());
        
        return kardex;
    }

    public static Kardex createNonCommercialEntry(Long factCode, String details, int quantity, 
                                                 BigDecimal unitPrice, Product product) {
        validateProduct(product);
        validateQuantity(quantity);
        
        Kardex kardex = new Kardex();
        kardex.setFactCode(factCode);
        kardex.setDate(ZonedDateTime.now());
        kardex.setDetails(details);
        kardex.setQuantity(quantity);
        kardex.setUnitPrice(unitPrice);
        kardex.setType(MovementType.nonCommercialEntry);
        kardex.setAvailableQuantity(quantity); 
        kardex.setProduct(product);
        kardex.setDetailsOutput(new ArrayList<>());
        kardex.setDetailsOrigin(new ArrayList<>());
        
        return kardex;
    }

    public static Kardex createNonCommercialExit(Long factCode, String details, int quantity, 
                                                BigDecimal unitPrice, Product product) {
        validateProduct(product);
        validateQuantity(quantity);
        
        Kardex kardex = new Kardex();
        kardex.setFactCode(factCode);
        kardex.setDate(ZonedDateTime.now());
        kardex.setDetails(details);
        kardex.setQuantity(quantity);
        kardex.setUnitPrice(unitPrice);
        kardex.setType(MovementType.nonCommercialExit); 
        kardex.setAvailableQuantity(0); 
        kardex.setProduct(product);
        kardex.setDetailsOutput(new ArrayList<>());
        kardex.setDetailsOrigin(new ArrayList<>());
        
        return kardex;
    }

    public static Kardex createPurchaseReturn(Long factCode, String details, int quantity, 
                                             BigDecimal unitPrice, Product product) {
        Kardex kardex = new Kardex();
        kardex.setFactCode(factCode);
        kardex.setDate(ZonedDateTime.now());
        kardex.setDetails(details);
        kardex.setQuantity(quantity);
        kardex.setUnitPrice(unitPrice);
        kardex.setType(MovementType.PURCHASE_RETURN);
        kardex.setAvailableQuantity(0); 
        kardex.setProduct(product);
        kardex.setDetailsOutput(new ArrayList<>());
        kardex.setDetailsOrigin(new ArrayList<>());
        
        return kardex;
    }
    public static Kardex createSaleReturn(Long factCode, String details, int quantity, 
                                         BigDecimal unitPrice, Product product) {
        Kardex kardex = new Kardex();
        kardex.setFactCode(factCode);
        kardex.setDate(ZonedDateTime.now());
        kardex.setDetails(details);
        kardex.setQuantity(quantity);
        kardex.setUnitPrice(unitPrice);
        kardex.setType(MovementType.SALES_RETURN);
        kardex.setAvailableQuantity(0); 
        kardex.setProduct(product);
        kardex.setDetailsOutput(new ArrayList<>());
        kardex.setDetailsOrigin(new ArrayList<>());
        
        return kardex;
    }

       
    public static Kardex createPurchaseAdjustment(Long factCode,String details, int quantity, 
                                       BigDecimal unitPrice, Product product, ZonedDateTime date) {
        validateProduct(product);
        validateQuantity(quantity);
        validateUnitPrice(unitPrice);
         validateFactCode(factCode);
        
        Kardex kardex = new Kardex();

        kardex.setDate(date);
        kardex.setDetails(details+" Factura:"+factCode);
        kardex.setQuantity(quantity);
        kardex.setUnitPrice(unitPrice);
        kardex.setType(MovementType.PURCHASE);
        kardex.setAvailableQuantity(quantity);
        kardex.setProduct(product);
        kardex.setDetailsOutput(new ArrayList<>());
        kardex.setDetailsOrigin(new ArrayList<>());
        
        return kardex;
    }
     public static Kardex createSaleAdjustment(Long factCode, String details, int quantity, 
                                   BigDecimal unitPrice, Product product,ZonedDateTime date) {
        validateProduct(product);
        validateQuantity(quantity);
        validateFactCode(factCode);
        
        Kardex kardex = new Kardex();
        kardex.setFactCode(factCode);
        kardex.setDate(date);
        kardex.setDetails(details+" Factura:"+factCode);
        kardex.setQuantity(quantity);
        kardex.setUnitPrice(unitPrice);
        kardex.setType(MovementType.SALE);
        kardex.setAvailableQuantity(0);
        kardex.setProduct(product);
        kardex.setDetailsOutput(new ArrayList<>());
        kardex.setDetailsOrigin(new ArrayList<>());
        
        return kardex;
    }

    public void addDate(){
        this.date = ZonedDateTime.now(ZoneId.of("America/Bogota"));
    }

    public void reduceAvailableQuantity(int amountToReduce) {
        if (amountToReduce <= 0) {
            throw new IllegalArgumentException("Amount to reduce must be positive");
        }
        if (this.availableQuantity < amountToReduce) {
            throw new IllegalArgumentException(
                String.format("Insufficient available quantity. Available: %d, Requested: %d", 
                    this.availableQuantity, amountToReduce)
            );
        }
        this.availableQuantity -= amountToReduce;
    }

    public void restoreAvailableQuantity(int amountToRestore) {
        if (amountToRestore <= 0) {
            throw new IllegalArgumentException("Amount to restore must be positive");
        }
        
        int newAvailable = this.availableQuantity + amountToRestore;
        if (newAvailable > this.quantity) {
            throw new IllegalArgumentException("Cannot restore more than original quantity");
        }
        
        this.availableQuantity = newAvailable;
    }

    public boolean canReduceQuantity(int amountToReduce) {
        return this.availableQuantity >= amountToReduce && amountToReduce > 0;
    }
    
    public boolean hasAvailableStock() {
        return this.availableQuantity > 0;
    }

    public boolean isPurchase() {
        return this.type == MovementType.PURCHASE;
    }
    
    public boolean isSale() {
        return this.type == MovementType.SALE;
    }
    
    public boolean isPurchaseReturn() {
        return this.type == MovementType.PURCHASE_RETURN;
    }
    
    public boolean isSaleReturn() {
        return this.type == MovementType.SALES_RETURN;
    }
    
  
    public boolean belongsToProduct(Long productId) {
        return this.product != null && this.product.getProductId().equals(productId);
    }
    

    public BigDecimal getTotalValue() {
        return this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
    

    public BigDecimal getAvailableValue() {
        return this.unitPrice.multiply(BigDecimal.valueOf(this.availableQuantity));
    }
    
    public void addOutputDetail(DetailOutput detail) {
        Objects.requireNonNull(detail, "Detail cannot be null");
        if (this.detailsOutput == null) {
            this.detailsOutput = new ArrayList<>();
        }
        this.detailsOutput.add(detail);
    }
    

    public void validateForPurchaseReturn(Long factCode, int quantity) {
        if (!this.isPurchase()) {
            throw new IllegalArgumentException("Return must be for a purchase movement");
        }
        if (!this.factCode.equals(factCode)) {
            throw new IllegalArgumentException("Invoice codes do not match");
        }
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Return quantity must be positive");
        }
        if (quantity > this.quantity) {
            throw new IllegalArgumentException(
                String.format("Cannot return more than purchased. Purchased: %d, Attempting to return: %d", 
                    this.quantity, quantity)
            );
        }

        if (quantity > this.availableQuantity) {
            throw new IllegalArgumentException(
                String.format("Cannot return products already sold. Available: %d, Attempting to return: %d", 
                    this.availableQuantity, quantity)
            );
        }
    

    }
    

    public void validateForSaleReturn(Long factCode, int quantityToReturn) {
        if (!this.isSale()) {
            throw new IllegalArgumentException("Return must be for a sale movement");
        }
        if (!this.factCode.equals(factCode)) {
            throw new IllegalArgumentException("Invoice codes do not match");
        }

        if (quantityToReturn <= 0) {
            throw new IllegalArgumentException("Return quantity must be positive");
        }
    
        if (quantityToReturn > this.quantity) {
            throw new IllegalArgumentException(
                String.format("Cannot return more than sold. Sold: %d, Attempting to return: %d", 
                    this.quantity, quantityToReturn)
            );
        }
    }
    
 
    public List<DetailOutput> getDetailsOutputReadOnly() {
        if (detailsOutput == null) return Collections.emptyList();
        return Collections.unmodifiableList(detailsOutput);
    }
    
    public List<DetailOutput> getDetailsOriginReadOnly() {
        if (detailsOrigin == null) return Collections.emptyList();
        return Collections.unmodifiableList(detailsOrigin);
    }
    
   
    private static void validateProduct(Product product) {
        Objects.requireNonNull(product, "Product cannot be null");
        product.validateForMovementCreation();
    }
    
    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }
    
    private static void validateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
    }
    
    private static void validateFactCode(Long factCode) {
        if (factCode == null || factCode <= 0) {
            throw new IllegalArgumentException("Fact code must be positive");
        }
    }

   
    public boolean isNonCommercialEntry() {
        return this.type == MovementType.nonCommercialEntry;
    }

    public boolean isNonCommercialExit() {
        return this.type == MovementType.nonCommercialExit;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kardex kardex = (Kardex) o;
        return Objects.equals(idKardex, kardex.idKardex);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idKardex);
    }
    
    @Override
    public String toString() {
        return String.format("Kardex{id=%d, type=%s, quantity=%d, available=%d}", 
            idKardex, type, quantity, availableQuantity);
    }
   
    
}
