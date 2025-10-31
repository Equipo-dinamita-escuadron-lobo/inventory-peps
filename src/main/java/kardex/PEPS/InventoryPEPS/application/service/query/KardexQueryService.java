package kardex.PEPS.InventoryPEPS.application.service.query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexQueryPort;
import kardex.PEPS.InventoryPEPS.domain.model.Balance;
import kardex.PEPS.InventoryPEPS.domain.model.InventoryQueue;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.KardexMigration;
import kardex.PEPS.InventoryPEPS.domain.model.KardexReport;
import kardex.PEPS.InventoryPEPS.domain.model.SaleDetail;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexQueryOutputPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KardexQueryService implements IKardexQueryPort {

     private final IKardexQueryOutputPort kardexQueryOutputPort;

    @Override
    public Page<KardexReport> getRecordsKardexByProduct(Long productId, LocalDate start, LocalDate end, Pageable pageable) {
        List<Kardex> prevMovements = kardexQueryOutputPort.findMovementsByProductBeforeDate(productId, start);
        List<Kardex> periodMovements = kardexQueryOutputPort.findMovementsByProductAndDateRange(productId, start, end);

        InventoryQueue inventoryQueue = new InventoryQueue();
        processInitialMovements(prevMovements, inventoryQueue);

        List<KardexReport> fullReport = generateKardexReports(periodMovements, inventoryQueue);

        return paginateResults(fullReport, pageable);
    }

    private void processInitialMovements(List<Kardex> movements, InventoryQueue queue) {
        for (Kardex movement : movements) {
            processMovementForQueue(movement, queue);
        }
    }

    private List<KardexReport> generateKardexReports(List<Kardex> movements, InventoryQueue queue) {
        List<KardexReport> reports = new ArrayList<>();

        for (Kardex movement : movements) {
            KardexReport report = processMovementToReport(movement, queue);
            reports.add(report);
        }

        return reports;
    }


    private KardexReport processMovementToReport(Kardex movement, InventoryQueue queue) {
        KardexReport report;

   
        if (movement.isPurchase() || movement.isSaleReturn() || movement.isNonCommercialEntry()) {
            report = handleEntryMovement(movement, queue);
        } 

        else if (movement.isSale() || movement.isNonCommercialExit()) {
            report = handleOutputMovement(movement, queue);
        } 
     
        else if (movement.isPurchaseReturn()) {
            report = handlePurchaseReturnMovement(movement, queue);
        } 
        else {
            // Fallback para tipos desconocidos
            report = handleUnknownMovement(movement, queue);
        }

        return report;
    }

  
    private KardexReport handleEntryMovement(Kardex movement, InventoryQueue queue) {
        Balance balance = Balance.fromMovement(movement.getQuantity(), movement.getUnitPrice());
        
       
        if (movement.isSaleReturn()) {
            queue.addBalanceFirst(balance);
        } else {
            queue.addBalance(balance);
        }
        
        return KardexReport.createEntryReport(movement, queue.getBalancesCopy());
    }

    private KardexReport handleOutputMovement(Kardex movement, InventoryQueue queue) {
        List<SaleDetail> details = queue.consumeQuantityForReport(movement.getQuantity());
        return KardexReport.createOutputReport(movement, details, queue.getBalancesCopy());
    }

   
    private KardexReport handlePurchaseReturnMovement(Kardex movement, InventoryQueue queue) {
        List<SaleDetail> returnDetails = queue.removeByUnitPrice(
            movement.getQuantity(), 
            movement.getUnitPrice()
        );
        return KardexReport.createOutputReport(movement, returnDetails, queue.getBalancesCopy());
    }

    
    private KardexReport handleUnknownMovement(Kardex movement, InventoryQueue queue) {
        // Determinar por availableQuantity como último recurso
        if (movement.getAvailableQuantity() > 0) {
            Balance balance = Balance.fromMovement(movement.getQuantity(), movement.getUnitPrice());
            queue.addBalance(balance);
            return KardexReport.createEntryReport(movement, queue.getBalancesCopy());
        } else {
            List<SaleDetail> details = queue.consumeQuantityForReport(movement.getQuantity());
            return KardexReport.createOutputReport(movement, details, queue.getBalancesCopy());
        }
    }

    private void processMovementForQueue(Kardex movement, InventoryQueue queue) {
       
        if (movement.isPurchase() || movement.isNonCommercialEntry()) {
            Balance balance = Balance.fromMovement(movement.getQuantity(), movement.getUnitPrice());
            queue.addBalance(balance);
        } 
        else if (movement.isSaleReturn()) {
            Balance balance = Balance.fromMovement(movement.getQuantity(), movement.getUnitPrice());
            queue.addBalanceFirst(balance);
        }
        //  SALIDAS: Venta + Salida no comercial
        else if (movement.isSale() || movement.isNonCommercialExit()) {
            queue.consumeQuantityForReport(movement.getQuantity());
        } 
        //  Devolución de compra
        else if (movement.isPurchaseReturn()) {
            queue.removeByUnitPrice(movement.getQuantity(), movement.getUnitPrice());
        }
    }

    private Page<KardexReport> paginateResults(List<KardexReport> fullReport, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        if (fullReport.size() < startItem) {
            return new PageImpl<>(Collections.emptyList(), pageable, fullReport.size());
        }

        int toIndex = Math.min(startItem + pageSize, fullReport.size());
        List<KardexReport> pagedList = fullReport.subList(startItem, toIndex);

        return new PageImpl<>(pagedList, pageable, fullReport.size());
    }

    private void validateEnterpriseId(String enterpriseId) {
        if (enterpriseId == null || enterpriseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Enterprise ID cannot be null or empty");
        }
    }

    @Override
    public List<Kardex> getKardexAvailableQuantityByProduct(Long productId) {
        return kardexQueryOutputPort.findAvailablePurchasesOrderedByDate(productId);
    }

    @Override
    public List<KardexMigration> findLastKardexForAllProducts(String enterpriseId) {
        validateEnterpriseId(enterpriseId);
        return kardexQueryOutputPort.findLastKardexForAllProducts(enterpriseId);
    }

    
}