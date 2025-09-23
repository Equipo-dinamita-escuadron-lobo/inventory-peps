package kardex.PEPS.InventoryPEPS.application.service;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexCommandPort;
import kardex.PEPS.InventoryPEPS.domain.enums.MovementType;
import kardex.PEPS.InventoryPEPS.domain.model.DetailOutput;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.port.output.IDetailQueryOutPutPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexCommandOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexQueryOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IProductQueryOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class KardexCommandService implements IKardexCommandPort {
    private final IKardexCommandOutputPort kardexCommandOutputPort;
    private final IProductQueryOutputPort productQueryOutputPort;
    private final IKardexQueryOutputPort kardexQueryOutputPort;
    private final IDetailQueryOutPutPort detailQueryOutPutPort;
    private final IFormatterResultOutputPort formatterResultOutputPort;
    

    //

    @Override
    public Kardex registerPurchase(Kardex kardex) {
       
        Optional<Product> product=existProductByProductId(kardex.getProduct().getProductId());
        verifyAmount(kardex.getQuantity());
        
        kardex.setProduct( product.get());
        kardex.setDate(ZonedDateTime.now());
        kardex.setType(MovementType.PURCHASE);
        kardex.setAvailableQuantity(kardex.getQuantity());
        return kardexCommandOutputPort.registerPurchase(kardex);
      
    }
    
    @Override
    public Kardex registerSale(Kardex kardexSaleRequest) {

        int quantityToSell = kardexSaleRequest.getQuantity();
        Long productId = kardexSaleRequest.getProduct().getProductId();
        verifyAmount(quantityToSell);
        Optional<Product> product=existProductByProductId(productId);
        
        // --- 1. PREPARACIÓN Y VALIDACIÓN ---

        // Obtenemos todos los lotes de compra con saldo disponible, ordenados por fecha (PEPS)
        List<Kardex> availablePurchases = kardexQueryOutputPort.findAvailablePurchasesOrderedByDate(product.get().getProductId());

        // Verificamos el stock total en memoria para evitar una consulta extra
        int totalAmountAvailable = availablePurchases.stream()
                                                    .mapToInt(Kardex::getAvailableQuantity)
                                                    .sum();
        if (totalAmountAvailable < quantityToSell) {
            log.info("Insufficient stock. Requested: " + quantityToSell + ", Available: " +  totalAmountAvailable);
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, "Insufficient stock. Requested: " + quantityToSell + ", Available: " +  totalAmountAvailable);
        }
        
        // --- 2. LÓGICA PEPS: PROCESAMIENTO EN MEMORIA ---

        List<DetailOutput> detailsToCreate = new ArrayList<>();
        List<Kardex> lotsToUpdate = new ArrayList<>(); // <-- Lista para registrar los lotes actualizados
        int remainingQuantityToSell = quantityToSell;

        for (Kardex purchaseLot : availablePurchases) {
            if (remainingQuantityToSell <= 0) {
                break; // Venta completada
            }

            int amountToTakeFromLot = Math.min(remainingQuantityToSell, purchaseLot.getAvailableQuantity());

            // Si se usa algo de este lote, se actualiza y se añade a la lista para persistir
            if (amountToTakeFromLot > 0) {
                int newAvailableAmount = purchaseLot.getAvailableQuantity() - amountToTakeFromLot;
                purchaseLot.setAvailableQuantity(newAvailableAmount);
                lotsToUpdate.add(purchaseLot); // <-- ¡Clave! Registrar el lote modificado

                // Creamos el detalle de la salida
                DetailOutput detail = new DetailOutput();
                detail.setAmountUsed(amountToTakeFromLot);
                detail.setUnitPrice(purchaseLot.getUnitPrice());
                detail.setMovementOrigin(purchaseLot); // Vinculamos al lote de COMPRA
                
                detailsToCreate.add(detail);
                
                remainingQuantityToSell -= amountToTakeFromLot;
            }
        }

        //creacion del movimiento de venta

        Kardex saleMovement = new Kardex();
       // saleMovement.setProduct(kardexSaleRequest.getProduct());
        saleMovement.setProduct(product.get());
        saleMovement.setFactCode(kardexSaleRequest.getFactCode());
        saleMovement.setDetails(kardexSaleRequest.getDetails());
        saleMovement.setQuantity(quantityToSell);
        saleMovement.setDate(ZonedDateTime.now());
        saleMovement.setType(MovementType.SALE);
        saleMovement.setAvailableQuantity(0); // Las ventas no tienen saldo
        saleMovement.setDetailsOutput(detailsToCreate); // Asignamos los detalles creados

        
        return kardexCommandOutputPort.registerSale(saleMovement,lotsToUpdate);

    }



    @Override
    public Kardex registerPurchaseReturn(Kardex kardex) {
        // 1. Obtener el movimiento de compra original usando el ID que viene en la petición
        //Optional<Kardex> kardexAuxOpt = kardexQueryOutputPort.findById(kardex.getIdKardex());

        Optional<Kardex> kardexAuxOpt = kardexQueryOutputPort.findByRefFacture(kardex.getFactCode(), kardex.getProduct().getProductId());

        // 2. Validar las reglas de negocio sobre el movimiento original
        if (kardexAuxOpt.isEmpty() || kardexAuxOpt.get().getQuantity() != kardexAuxOpt.get().getAvailableQuantity()) {
            log.info("Products sold cannot be returned.");
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, "Products sold cannot be returned.");
        }
        if(!kardexAuxOpt.get().getType().equals(MovementType.PURCHASE)){
            log.info("The return must be for a purchase."+kardexAuxOpt.get().getIdKardex()+"it does not belong to a purchase transaction");
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, "The return must be for a purchase."+kardexAuxOpt.get().getIdKardex()+"it does not belong to a purchase transaction");
        }
        if(!kardexAuxOpt.get().getFactCode().equals(kardex.getFactCode())){
            log.info("The invoice codes do not match" );
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400,"The invoice codes do not match" );
        }

        Kardex originalPurchase = kardexAuxOpt.get();

        if (kardex.getQuantity() != originalPurchase.getQuantity()) {
            log.info("The quantity does not match.");
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, "The quantity does not match.");
        }
        
        

        // 3. Actualizar la disponibilidad del registro de compra original a 0
        kardexCommandOutputPort.updateAvaliableAmount(originalPurchase.getIdKardex(), 0);

        Optional<Product> product=productQueryOutputPort.getProductByProductId(kardex.getProduct().getProductId());

        // 4. Crear un OBJETO NUEVO para el registro de la devolución
        Kardex returnMovement = new Kardex();
        returnMovement.setProduct(product.get()); // Usar el mismo producto
        returnMovement.setFactCode(originalPurchase.getFactCode()); // Referenciar la misma factura
        returnMovement.setDetails(kardex.getDetails());
        returnMovement.setQuantity(originalPurchase.getQuantity());
        returnMovement.setUnitPrice(originalPurchase.getUnitPrice());
        returnMovement.setDate(ZonedDateTime.now());
        returnMovement.setType(MovementType.PURCHASE_RETURN);
        returnMovement.setAvailableQuantity(0); // Las devoluciones no tienen saldo disponible

        // 5. Guardar el NUEVO movimiento. Como returnMovement no tiene ID, JPA hará un INSERT.
        return kardexCommandOutputPort.registerPurchaseReturn(returnMovement);
    }

  



    @Override
    public List<Kardex> registerSaleReturn(Kardex kardex) {
       
      // Obtener la venta original
        //Optional<Kardex> originalSaleOpt = kardexQueryOutputPort.findById(kardex.getIdKardex());
        Optional<Kardex> originalSaleOpt = kardexQueryOutputPort.findByRefFacture(kardex.getFactCode(), kardex.getProduct().getProductId());
      
        if (originalSaleOpt.isEmpty()) {
            log.info("Sale to be returned not found.");
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, "Sale to be returned not found.");
        }

        Kardex originalSale = originalSaleOpt.get();
        
        if (!originalSale.getType().equals(MovementType.SALE)) {
            log.info("The movement is not a sale.");
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, "The movement is not a sale.");
             return null;
        }
        if (!originalSale.getFactCode().equals(kardex.getFactCode())) {
            log.info("Invoice codes do not match.");
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, "Invoice codes do not match.");
            return null;
        }

        // Obtener los detalles de los lotes de compra que se usaron en esta venta
        List<DetailOutput> detailsOfSale = detailQueryOutPutPort.findByMovementSale(originalSale.getIdKardex());

        if (detailsOfSale.isEmpty()) {
            log.info("Sale has no details to return or was already returned.");
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, "Sale has no details to return or was already returned.");
            return null;
        }

        List<Kardex> createdReturnMovements = new ArrayList<>();

      

        
       for(DetailOutput detail: detailsOfSale){
            if(kardex.getQuantity()==detail.getAmountUsed() 
            && kardex.getFactCode().equals(originalSale.getFactCode())){

                Kardex originalPurchaseLot = detail.getMovementOrigin();
                int newAvailableAmount = originalPurchaseLot.getAvailableQuantity() + detail.getAmountUsed();
                kardexCommandOutputPort.updateAvaliableAmount(originalPurchaseLot.getIdKardex(), newAvailableAmount);
                Optional<Product> product=productQueryOutputPort.getProductByProductId(kardex.getProduct().getProductId());
               
                Kardex returnMovement = new Kardex();
                returnMovement.setProduct(product.get());
                returnMovement.setFactCode(originalSale.getFactCode());
                returnMovement.setDetails(kardex.getDetails());
                returnMovement.setQuantity(detail.getAmountUsed());
                returnMovement.setUnitPrice(detail.getUnitPrice()); // Precio de la venta
                returnMovement.setDate(ZonedDateTime.now());
                returnMovement.setType(MovementType.SALES_RETURN);
                returnMovement.setAvailableQuantity(0);

                Kardex createdReturn = kardexCommandOutputPort.registerSaleReturn(returnMovement);
                createdReturnMovements.add(createdReturn);

                detailQueryOutPutPort.deleteById(detail.getIdDetailOutput());
             
            }
       }
           return createdReturnMovements;
    }


    private Optional<Product> existProductByProductId(Long productId){
        Optional<Product> product=productQueryOutputPort.getProductByProductId(productId);
        if(product.isEmpty()){
            log.info("Product not found");
            formatterResultOutputPort.returnEntityDoesNotExistErrorResponse(404, "Product not found");
        }
        return product;

    }
    private void verifyAmount(int amount){
        if(amount<=0){
            log.info("The aumount must be greater than 0");
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400,"The aumount must be greater than 0");
        }
  
    }
    
}
