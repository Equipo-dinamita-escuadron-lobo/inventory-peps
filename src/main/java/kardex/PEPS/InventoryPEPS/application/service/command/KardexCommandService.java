package kardex.PEPS.InventoryPEPS.application.service.command;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexCommandPort;
import kardex.PEPS.InventoryPEPS.domain.model.DetailOutput;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.port.output.IDetailQueryOutPutPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexCommandOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexQueryOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IProductEventPort;
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
    private final KardexAdjustmentValidationService kardexAdjustmentValidationService;
    private final IProductEventPort productEventPort;


    @Override
    public Kardex registerPurchase(Kardex kardexRequest) {
       
        Product product = getValidatedProduct(kardexRequest.getProduct().getProductId());
        
        
        Kardex purchase = Kardex.createPurchase(
            kardexRequest.getFactCode(),
            kardexRequest.getDetails(),
            kardexRequest.getQuantity(),
            kardexRequest.getUnitPrice(),
            product
        );
        /* 

        if(!kardexQueryOutputPort.existsByProduct_ProductId(product.getProductId())){

        }
        */ 
      

     //Publicar evento de uso de producto
      productEventPort.publishUsedProductEvent(kardexRequest.getProduct().getProductId(), 1);

        return kardexCommandOutputPort.registerPurchase(purchase);  
    }
    
    @Override
    public Kardex registerSale(Kardex kardexSaleRequest) {

        // Obtener y validar producto
        Product product = getValidatedProduct(kardexSaleRequest.getProduct().getProductId());
        
        // Obtener lotes disponibles
        List<Kardex> availablePurchases = kardexQueryOutputPort
            .findAvailablePurchasesOrderedByDate(product.getProductId());
        
        //Validar stock total disponible usando el dominio
        validateSufficientStock(availablePurchases, kardexSaleRequest.getQuantity());
        
        //Crear movimiento de venta usando 
        Kardex saleMovement = Kardex.createSale(
            kardexSaleRequest.getFactCode(),
            kardexSaleRequest.getDetails(),
            kardexSaleRequest.getQuantity(),
            kardexSaleRequest.getUnitPrice(),
            product
        );
        
        //Procesar lógica FIFO usando métodos de dominio
        FIFOResult fifoResult = processFIFOLogic(availablePurchases, saleMovement);
        
        // Agregar detalles al movimiento de venta usando método de dominio
        fifoResult.detailsToCreate.forEach(saleMovement::addOutputDetail);
        
        return kardexCommandOutputPort.registerSale(saleMovement, fifoResult.lotsToUpdate);

    }



    @Override
    public Kardex registerPurchaseReturn(Kardex kardexRequest) {
        // Obtener compra original
        Kardex originalPurchase = kardexQueryOutputPort
            .findByRefFacture(kardexRequest.getFactCode(), kardexRequest.getProduct().getProductId())
            .orElseThrow(() -> {
                log.info("Original purchase not found for return");
                formatterResultOutputPort.returnEntityDoesNotExistErrorResponse(404, "Original purchase not found");
                return new IllegalArgumentException("Original purchase not found");
            });
        
        // Validar usando método de dominio
        try {
            originalPurchase.validateForPurchaseReturn(kardexRequest.getFactCode(), kardexRequest.getQuantity());
        } catch (IllegalArgumentException e) {
            log.info("Purchase return validation failed: {}", e.getMessage());
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, e.getMessage());
            throw e;
        }
        
        //Actualizar disponibilidad del lote 
        kardexCommandOutputPort.updateAvaliableAmount(originalPurchase.getIdKardex(), originalPurchase.getQuantity()-kardexRequest.getQuantity());
        
        //  Obtener producto
        Product product = getValidatedProduct(kardexRequest.getProduct().getProductId());
        
        // Crear devolución usando Factory Method
        Kardex returnMovement = Kardex.createPurchaseReturn(
            originalPurchase.getFactCode(),
            kardexRequest.getDetails(),
            kardexRequest.getQuantity(),
            originalPurchase.getUnitPrice(),
            product
        );
        
        return kardexCommandOutputPort.registerPurchaseReturn(returnMovement);
    }

  



    @Override
    public List<Kardex> registerSaleReturn(Kardex kardexRequest) {
        // 1. Obtener venta original
        Kardex originalSale = kardexQueryOutputPort
            .findByRefFacture(kardexRequest.getFactCode(), kardexRequest.getProduct().getProductId())
            .orElseThrow(() -> {
                log.info("Sale to be returned not found");
                formatterResultOutputPort.returnBusinessRuleErrorResponse(400, "Sale to be returned not found");
                return new IllegalArgumentException("Sale to be returned not found");
            });
        
        // 2.Validar usando método de dominio actualizado
        try {
            originalSale.validateForSaleReturn(kardexRequest.getFactCode(), kardexRequest.getQuantity());
        } catch (IllegalArgumentException e) {
            log.info("Sale return validation failed: {}", e.getMessage());
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, e.getMessage());
            throw e;
        }
        
        // 3. Obtener detalles de la venta (ordenados por ID para obtener LIFO)
        List<DetailOutput> detailsOfSale = detailQueryOutPutPort.findByMovementSaleOrderedDesc(originalSale.getIdKardex());
        
        if (detailsOfSale.isEmpty()) {
            log.info("Sale has no details to return");
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, "Sale has no details to return");
            return new ArrayList<>();
        }
        
        // 4.Procesar devolución LIFO (desde el último lote vendido)
        return processSaleReturnLIFO(kardexRequest, originalSale, detailsOfSale);
    }

    /**
     *  Procesa devolución de venta con lógica LIFO
     */
    private List<Kardex> processSaleReturnLIFO(Kardex kardexRequest, Kardex originalSale, 
                                            List<DetailOutput> detailsOfSale) {
        List<Kardex> createdReturnMovements = new ArrayList<>();
        Product product = getValidatedProduct(kardexRequest.getProduct().getProductId());
        
        int remainingToReturn = kardexRequest.getQuantity();
        
        //  Recorrer detalles desde el último vendido (LIFO)
        for (DetailOutput detail : detailsOfSale) {
            if (remainingToReturn <= 0) {
                break; // Ya devolvimos todo
            }
            
            // Cantidad a devolver de este lote específico
            int amountFromThisLot = Math.min(remainingToReturn, detail.getQuantityUsed());
            
            // Restaurar cantidad en lote original
            Kardex originalPurchaseLot = detail.getMovementOrigin();
            originalPurchaseLot.restoreAvailableQuantity(amountFromThisLot);
            kardexCommandOutputPort.updateAvaliableAmount(
                originalPurchaseLot.getIdKardex(), 
                originalPurchaseLot.getAvailableQuantity()
            );
            
            //Crear movimiento de devolución para este lote
            Kardex returnMovement = Kardex.createSaleReturn(
                originalSale.getFactCode(),
                kardexRequest.getDetails(),
                amountFromThisLot, 
                detail.getUnitPrice(),
                product
            );
            
            Kardex createdReturn = kardexCommandOutputPort.registerSaleReturn(returnMovement);
            createdReturnMovements.add(createdReturn);
            
            //Actualizar o eliminar el detalle original
            if (amountFromThisLot == detail.getQuantityUsed()) {
                // Se devolvió todo el lote
                detailQueryOutPutPort.deleteById(detail.getIdDetailOutput());
            } else {
                // Devolución parcial
                int newAmount = detail.getQuantityUsed() - amountFromThisLot;
                detail.setQuantityUsed(newAmount);
                detail.setUnitPrice(detail.getUnitPrice().multiply(BigDecimal.valueOf(newAmount)));
                detailQueryOutPutPort.update(detail); 
            }
            
            remainingToReturn -= amountFromThisLot;
        }
        
        return createdReturnMovements;
    }



    @Override
    public Kardex registerNonCommercialExit(Kardex kardexRequest) {
         //Obtener y validar producto
        Product product = getValidatedProduct(kardexRequest.getProduct().getProductId());
        
        //Obtener lotes disponibles
        List<Kardex> availablePurchases = kardexQueryOutputPort
            .findAvailablePurchasesOrderedByDate(product.getProductId());
        
        //Validar stock disponible
        validateSufficientStock(availablePurchases, kardexRequest.getQuantity());
        
        //Crear salida no comercial usando Factory Method
        Kardex nonCommercialExit = Kardex.createNonCommercialExit(
            kardexRequest.getFactCode(),
            kardexRequest.getDetails(),
            kardexRequest.getQuantity(),
            kardexRequest.getUnitPrice(),
            product
        );
        
        // Procesar lógica FIFO
        FIFOResult fifoResult = processFIFOLogic(availablePurchases, nonCommercialExit);
        
        //Agregar detalles usando método de dominio
        fifoResult.detailsToCreate.forEach(nonCommercialExit::addOutputDetail);
        
        return kardexCommandOutputPort.registerNonCommercialExit(nonCommercialExit, fifoResult.lotsToUpdate);

    }

    @Override
    public Kardex registerNonCommercialEntry(Kardex kardexRequest) {
       
         //Obtener y validar producto
        Product product = getValidatedProduct(kardexRequest.getProduct().getProductId());
        
        //Crear entrada no comercial usando Factory Method (incluye validaciones)
        Kardex nonCommercialEntry = Kardex.createNonCommercialEntry(
            kardexRequest.getFactCode(),
            kardexRequest.getDetails(),
            kardexRequest.getQuantity(),
            kardexRequest.getUnitPrice(),
            product
        );
        
        //Publicar evento de uso de producto
        productEventPort.publishUsedProductEvent(kardexRequest.getProduct().getProductId(), 1);

        return kardexCommandOutputPort.registerNonCommercialEntry(nonCommercialEntry);
    }

    @Override
    public Kardex registerAdjustmentEntry(Kardex kardexRequest) {
        Product product = getValidatedProduct(kardexRequest.getProduct().getProductId());
        
        kardexAdjustmentValidationService.validateDateForAdjustment(kardexRequest,product.getEnterpriseId());

       
        Kardex purchaseAdjustment = Kardex.createPurchaseAdjustment(
            kardexRequest.getFactCode(),
            kardexRequest.getDetails(),
            kardexRequest.getQuantity(),
            kardexRequest.getUnitPrice(),
            product,
            kardexRequest.getDate()
        );

        //Publicar evento de uso de producto
        productEventPort.publishUsedProductEvent(kardexRequest.getProduct().getProductId(), 1);
        
        return kardexCommandOutputPort.registerPurchase(purchaseAdjustment);
    }

    @Override
    public Kardex registerAdjustmentExit(Kardex kardex) {
        // Obtener y validar producto
        Product product = getValidatedProduct(kardex.getProduct().getProductId());
        
        // Obtener lotes disponibles
        List<Kardex> availablePurchases = kardexQueryOutputPort
            .findAvailablePurchasesOrderedByDate(product.getProductId());
        
        //Validar stock total disponible usando el dominio
        validateSufficientStock(availablePurchases, kardex.getQuantity());
        
         kardexAdjustmentValidationService.validateDateForAdjustment(kardex,product.getEnterpriseId());
    

        //Crear movimiento de venta usando 
        Kardex saleMovement = Kardex.createSaleAdjustment(
            kardex.getFactCode(),
            kardex.getDetails(),
            kardex.getQuantity(),
            kardex.getUnitPrice(),
            product,
            kardex.getDate()
        );
        
        //Procesar lógica FIFO usando métodos de dominio
        FIFOResult fifoResult = processFIFOLogic(availablePurchases, saleMovement);
        
        // Agregar detalles al movimiento de venta usando método de dominio
        fifoResult.detailsToCreate.forEach(saleMovement::addOutputDetail);
        
        return kardexCommandOutputPort.registerSale(saleMovement, fifoResult.lotsToUpdate);


    }

  

    @Override
    public void deleteAll() {
       log.info("Deleting all kardex records");
        try {
            kardexCommandOutputPort.deleteAll();
            log.info("All kardex records deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting all kardex records: {}", e.getMessage());
        }

    }


    /**
     * Obtiene y valida un producto usando el dominio
     */
    private Product getValidatedProduct(Long productId) {
        return productQueryOutputPort.getProductByProductId(productId)
            .orElseThrow(() -> {
                log.info("Product not found: {}", productId);
                formatterResultOutputPort.returnEntityDoesNotExistErrorResponse(404, "Product not found");
                return new IllegalArgumentException("Product not found");
            });
    }

    /**
     * Valida que hay suficiente stock usando lógica de dominio
     */
    private void validateSufficientStock(List<Kardex> availablePurchases, int quantityRequested) {
        int totalAvailable = availablePurchases.stream()
            .mapToInt(Kardex::getAvailableQuantity)
            .sum();
            
        if (totalAvailable < quantityRequested) {
            String message = String.format("Insufficient stock. Requested: %d, Available: %d", 
                quantityRequested, totalAvailable);
            log.info(message);
            formatterResultOutputPort.returnBusinessRuleErrorResponse(400, message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Procesa la lógica FIFO usando métodos de dominio
     */
    private FIFOResult processFIFOLogic(List<Kardex> availablePurchases, Kardex outputMovement) {
        List<DetailOutput> detailsToCreate = new ArrayList<>();
        List<Kardex> lotsToUpdate = new ArrayList<>();
        int remainingQuantity = outputMovement.getQuantity();

        for (Kardex purchaseLot : availablePurchases) {
            if (remainingQuantity <= 0) {
                break;
            }

            int amountFromThisLot = Math.min(remainingQuantity, purchaseLot.getAvailableQuantity());

            if (amountFromThisLot > 0) {
                //Usar método para reducir cantidad (incluye validaciones)
                purchaseLot.reduceAvailableQuantity(amountFromThisLot);
                lotsToUpdate.add(purchaseLot);

                //Crear detalle usando Factory Method de dominio
                DetailOutput detail = DetailOutput.create(
                    amountFromThisLot,
                    purchaseLot.getUnitPrice(),
                    outputMovement,
                    purchaseLot
                );
                detailsToCreate.add(detail);

                remainingQuantity -= amountFromThisLot;
            }
        }

        return new FIFOResult(detailsToCreate, lotsToUpdate);
    }

   

 

    //Record para resultado FIFO
    private record FIFOResult(List<DetailOutput> detailsToCreate, List<Kardex> lotsToUpdate) {}





   

   

}
