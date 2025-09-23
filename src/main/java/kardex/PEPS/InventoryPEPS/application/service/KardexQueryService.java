package kardex.PEPS.InventoryPEPS.application.service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator; // Importante para remover elementos de forma segura
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexQueryPort;
import kardex.PEPS.InventoryPEPS.domain.enums.MovementType;
import kardex.PEPS.InventoryPEPS.domain.model.Balance;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.KardexReport;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexQueryOutputPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KardexQueryService implements IKardexQueryPort {

    private final IKardexQueryOutputPort kardexQueryOutputPort;

    @Override
    public Page<KardexReport> getRecordsKardexByProduct(Long productId, LocalDate start, LocalDate end,Pageable pageable) {
         // --- PASO 1: Calcular la lista COMPLETA de reportes (Lógica original) ---

        // Fase 1: Obtener movimientos anteriores para saldo inicial
        List<Kardex> prevMovements = kardexQueryOutputPort.findMovementsByProductBeforeDate(productId, start);

        // Fase 2: Obtener TODOS los movimientos dentro de rango (sin paginación de BD)
        // Asegúrate de tener un método que devuelva List, no Page.
        List<Kardex> periodMovements = kardexQueryOutputPort.findMovementsByProductAndDateRange(productId, start, end);

        // Cola para procesar el inventario
        LinkedList<Balance> inventoryQueue = new LinkedList<>();
        processMovements(prevMovements, inventoryQueue);

        // Lista para almacenar el reporte completo
        List<KardexReport> fullReport = new ArrayList<>();

        // Saldo inicial
        /* 
        KardexReport initialRow = new KardexReport();
        ZonedDateTime zonedStart = start.atStartOfDay(ZoneId.systemDefault());
        initialRow.setDate(zonedStart);
        initialRow.setDetail("SALDO INICIAL");
        initialRow.setBalance(copyBalance(inventoryQueue));
        updateRowTotals(initialRow, initialRow.getBalance());
        fullReport.add(initialRow);*/

        // Procesar todos los movimientos del periodo
        for (Kardex mov : periodMovements) {
            KardexReport row = new KardexReport();
            row.setDate(mov.getDate());
            row.setDetail(mov.getDetails());

            if (mov.getType() == MovementType.PURCHASE) {
                handlePurchase(mov, inventoryQueue, row);
            } else if (mov.getType() == MovementType.SALE) {
                handleSale(mov, inventoryQueue, row);
            } else if (mov.getType() == MovementType.PURCHASE_RETURN) {
                handlePurchaseReturn(mov, inventoryQueue, row);
            } else if (mov.getType() == MovementType.SALES_RETURN) {
                handleSalesReturn(mov, inventoryQueue, row);
            }

            row.setBalance(copyBalance(inventoryQueue));
            updateRowTotals(row, row.getBalance());
            fullReport.add(row);
        }

        // --- PASO 2: Paginar la lista COMPLETA en memoria ---

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<KardexReport> pagedList;

        if (fullReport.size() < startItem) {
            // Si el índice de inicio está fuera del tamaño de la lista, devuelve una lista vacía.
            pagedList = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, fullReport.size());
            pagedList = fullReport.subList(startItem, toIndex);
        }

        // --- PASO 3: Crear y devolver un objeto Page ---
        // Se usa PageImpl para construir una implementación de Page.
        return new PageImpl<>(pagedList, pageable, fullReport.size());
    }

    // Procesa todos los movimientos para construir el estado de la cola
    private void processMovements(List<Kardex> movements, LinkedList<Balance> inventoryQueue) {
        for (Kardex mov : movements) {
            if (mov.getType() == MovementType.PURCHASE) {
                handlePurchase(mov, inventoryQueue, null);
            } else if (mov.getType() == MovementType.SALE) {
                handleSale(mov, inventoryQueue, null);
            } else if (mov.getType() == MovementType.PURCHASE_RETURN) {
                handlePurchaseReturn(mov, inventoryQueue, null);
            } else if (mov.getType() == MovementType.SALES_RETURN) {
                handleSalesReturn(mov, inventoryQueue, null);
            }
        }
    }

    // --- Lógica para cada tipo de movimiento ---

    private void handlePurchase(Kardex mov, LinkedList<Balance> queue, KardexReport row) {
        BigDecimal total = mov.getUnitPrice().multiply(new BigDecimal(mov.getQuantity()));
        queue.add(new Balance(mov.getQuantity(), mov.getUnitPrice(), total));
        if (row != null) {
            row.setEntryQuantity(mov.getQuantity());
            row.setEntryUnitPrice(mov.getUnitPrice());
            row.setEntryTotalPrice(total);
        }
    }

    private void handleSale(Kardex mov, LinkedList<Balance> queue, KardexReport row) {
        if (row != null) {
            row.setOutputQuantity(mov.getQuantity());
            BigDecimal totalCost = BigDecimal.ZERO;
            if (mov.getDetailsOutput() != null) {
                for (var det : mov.getDetailsOutput()) {
                    totalCost = totalCost.add(det.getUnitPrice().multiply(BigDecimal.valueOf(det.getAmountUsed())));
                }
            }
            row.setOutputTotalPrice(totalCost);
            row.setOutputUnitPrice(mov.getQuantity() != 0
                    ? totalCost.divide(BigDecimal.valueOf(mov.getQuantity()), BigDecimal.ROUND_HALF_UP)
                    : BigDecimal.ZERO);
        }
        
        int remain = mov.getQuantity();
        while (remain > 0 && !queue.isEmpty()) {
            Balance lote = queue.peekFirst();
            if (lote.getAmount() <= remain) {
                remain -= lote.getAmount();
                queue.pollFirst();
            } else {
                lote.setAmount(lote.getAmount() - remain);
                lote.setTotalPrice(lote.getUnitPrice().multiply(BigDecimal.valueOf(lote.getAmount())));
                remain = 0;
            }
        }
    }

    private void handleSalesReturn(Kardex mov, LinkedList<Balance> queue, KardexReport row) {
        // Una devolución de venta es una ENTRADA que se coloca al PRINCIPIO de la cola.
        BigDecimal total = mov.getUnitPrice().multiply(new BigDecimal(mov.getQuantity()));
        queue.addFirst(new Balance(mov.getQuantity(), mov.getUnitPrice(), total));
        if (row != null) {
            row.setEntryQuantity(mov.getQuantity());
            row.setEntryUnitPrice(mov.getUnitPrice());
            row.setEntryTotalPrice(total);
        }
    }

    private void handlePurchaseReturn(Kardex mov, LinkedList<Balance> queue, KardexReport row) {
        // Una devolución de compra es una SALIDA de un lote específico, no necesariamente el primero.
        if (row != null) {
            row.setOutputQuantity(mov.getQuantity());
            row.setOutputUnitPrice(mov.getUnitPrice());
            row.setOutputTotalPrice(mov.getUnitPrice().multiply(new BigDecimal(mov.getQuantity())));
        }

        int amountToRemove = mov.getQuantity();
        Iterator<Balance> iterator = queue.iterator();
        while (iterator.hasNext() && amountToRemove > 0) {
            Balance lote = iterator.next();

            // Buscamos el lote por el costo unitario.
            // Para un sistema real, sería más robusto buscar por un ID de lote.
            if (lote.getUnitPrice().compareTo(mov.getUnitPrice()) == 0) {
                if (lote.getAmount() <= amountToRemove) {
                    // Si el lote se consume completamente, lo eliminamos.
                    amountToRemove -= lote.getAmount();
                    iterator.remove();
                } else {
                    // Si el lote se consume parcialmente, actualizamos su cantidad.
                    lote.setAmount(lote.getAmount() - amountToRemove);
                    lote.setTotalPrice(lote.getUnitPrice().multiply(BigDecimal.valueOf(lote.getAmount())));
                    amountToRemove = 0;
                }
            }
        }
    }

    // --- Métodos de utilidad (sin cambios) ---

    private List<Balance> copyBalance(LinkedList<Balance> queue) {
        List<Balance> copy = new ArrayList<>();
        for (Balance b : queue)
            copy.add(new Balance(b.getAmount(), b.getUnitPrice(), b.getTotalPrice()));
        return copy;
    }

    private void updateRowTotals(KardexReport row, List<Balance> balance) {
        int totalAmount = balance.stream().mapToInt(Balance::getAmount).sum();
        BigDecimal totalValue = balance.stream()
                .map(Balance::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        row.setTotalBalanceQuantity(totalAmount);
        row.setTotalBalanceValue(totalValue);
    }
}