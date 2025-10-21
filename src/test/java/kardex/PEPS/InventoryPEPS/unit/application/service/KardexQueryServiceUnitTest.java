package kardex.PEPS.InventoryPEPS.unit.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import kardex.PEPS.InventoryPEPS.application.service.query.KardexQueryService;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.KardexReport;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexQueryOutputPort;
import static org.mockito.BDDMockito.given;




@ExtendWith(MockitoExtension.class)
public class KardexQueryServiceUnitTest {
    
    @Mock
    private IKardexQueryOutputPort kardexQueryOutputPort;

    @InjectMocks
    private KardexQueryService kardexQueryService;

    private Product mockProduct;
    private LocalDate startDate;
    private LocalDate endDate;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setProductId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setState(true);

        startDate = LocalDate.of(2025, 1, 1);
        endDate = LocalDate.of(2025, 1, 31);
        pageable = PageRequest.of(0, 10);
    }

    // ==================== getRecordsKardexByProduct() - Basic ====================
    @Test
    @DisplayName("Should generate kardex report with no previous movements")
    void testGetRecordsKardexByProduct_NoPreviousMovements_ReturnsReport() {
        // Given
        List<Kardex> prevMovements = new ArrayList<>();
        List<Kardex> periodMovements = createPurchaseMovements();

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(prevMovements);
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(periodMovements);

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(kardexQueryOutputPort).findMovementsByProductBeforeDate(1L, startDate);
        verify(kardexQueryOutputPort).findMovementsByProductAndDateRange(1L, startDate, endDate);
    }

    @Test
    @DisplayName("Should process previous movements to calculate initial balance")
    void testGetRecordsKardexByProduct_WithPreviousMovements_CalculatesInitialBalance() {
        // Given
        List<Kardex> prevMovements = createPurchaseMovements();
        List<Kardex> periodMovements = createSaleMovements();

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(prevMovements);
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(periodMovements);

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        KardexReport firstReport = result.getContent().get(0);
        assertTrue(firstReport.hasBalance());
    }

    // ==================== Purchase Movements ====================
    @Test
    @DisplayName("Should handle purchase movement correctly")
    void testGetRecordsKardexByProduct_PurchaseMovement_CreatesEntryReport() {
        // Given
        Kardex purchase = Kardex.createPurchase(
            1001L,
            "Purchase",
            100,
            new BigDecimal("10.00"),
            mockProduct
        );
        purchase.setDate(ZonedDateTime.now());

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(new ArrayList<>());
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(List.of(purchase));

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        KardexReport report = result.getContent().get(0);
        assertTrue(report.isEntry());
        assertEquals(100, report.getEntryQuantity());
    }

    @Test
    @DisplayName("Should handle multiple purchase movements")
    void testGetRecordsKardexByProduct_MultiplePurchases_AccumulatesBalance() {
        // Given
        Kardex purchase1 = Kardex.createPurchase(1001L, "Purchase 1", 100, new BigDecimal("10.00"), mockProduct);
        purchase1.setDate(ZonedDateTime.now().minusDays(2));
        
        Kardex purchase2 = Kardex.createPurchase(1002L, "Purchase 2", 50, new BigDecimal("12.00"), mockProduct);
        purchase2.setDate(ZonedDateTime.now().minusDays(1));

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(new ArrayList<>());
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(List.of(purchase1, purchase2));

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        KardexReport lastReport = result.getContent().get(1);
        assertEquals(150, lastReport.getTotalBalanceQuantity()); // 100 + 50
    }

    
    @Test
    @DisplayName("handle sale movement with FIFO consumption")
    void testGetRecordsKardexByProduct_SaleMovement_ConsumesFromQueue() {
        // Given
        Kardex purchase = Kardex.createPurchase(1001L, "Purchase", 100, new BigDecimal("10.00"), mockProduct);
        purchase.setDate(ZonedDateTime.now().minusDays(2));
        
        Kardex sale = Kardex.createSale(2001L, "Sale", 30, new BigDecimal("15.00"), mockProduct);
        sale.setDate(ZonedDateTime.now().minusDays(1));

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(List.of(purchase));
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(List.of(sale));

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        KardexReport report = result.getContent().get(0);
        assertTrue(report.isOutput());
        assertEquals(30, report.getOutputQuantity());
        assertEquals(70, report.getTotalBalanceQuantity()); // 100 - 30
    }

    @Test
    @DisplayName("Should handle sale consuming from multiple lots")
    void testGetRecordsKardexByProduct_SaleMultipleLots_ProcessesFIFO() {
        // Given
        Kardex purchase1 = Kardex.createPurchase(1001L, "Purchase 1", 50, new BigDecimal("10.00"), mockProduct);
        purchase1.setDate(ZonedDateTime.now().minusDays(3));
        
        Kardex purchase2 = Kardex.createPurchase(1002L, "Purchase 2", 50, new BigDecimal("12.00"), mockProduct);
        purchase2.setDate(ZonedDateTime.now().minusDays(2));
        
        Kardex sale = Kardex.createSale(2001L, "Sale", 80, new BigDecimal("15.00"), mockProduct);
        sale.setDate(ZonedDateTime.now().minusDays(1));

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(List.of(purchase1, purchase2));
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(List.of(sale));

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        KardexReport report = result.getContent().get(0);
        assertEquals(20, report.getTotalBalanceQuantity()); // 100 - 80
        assertEquals(2, report.getOutputDetails().size()); // Details from 2 lots
    }

    // ==================== Purchase Return ====================
    @Test
    @DisplayName("Should handle purchase return by removing from queue by price")
    void testGetRecordsKardexByProduct_PurchaseReturn_RemovesByUnitPrice() {
        // Given
        Kardex purchase = Kardex.createPurchase(1001L, "Purchase", 100, new BigDecimal("10.00"), mockProduct);
        purchase.setDate(ZonedDateTime.now().minusDays(2));
        
        Kardex purchaseReturn = Kardex.createPurchaseReturn(1001L, "Return", 20, new BigDecimal("10.00"), mockProduct);
        purchaseReturn.setDate(ZonedDateTime.now().minusDays(1));

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(List.of(purchase));
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(List.of(purchaseReturn));

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        KardexReport report = result.getContent().get(0);
        assertTrue(report.isOutput());
        assertEquals(80, report.getTotalBalanceQuantity()); // 100 - 20
    }

    // ==================== Sale Return ====================
    @Test
    @DisplayName("Should handle sale return by adding to beginning of queue")
    void testGetRecordsKardexByProduct_SaleReturn_AddsToQueueFirst() {
        // Given
        Kardex purchase = Kardex.createPurchase(1001L, "Purchase", 100, new BigDecimal("10.00"), mockProduct);
        purchase.setDate(ZonedDateTime.now().minusDays(3));
        
        Kardex sale = Kardex.createSale(2001L, "Sale", 30, new BigDecimal("15.00"), mockProduct);
        sale.setDate(ZonedDateTime.now().minusDays(2));
        
        Kardex saleReturn = Kardex.createSaleReturn(2001L, "Sale Return", 10, new BigDecimal("10.00"), mockProduct);
        saleReturn.setDate(ZonedDateTime.now().minusDays(1));

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(List.of(purchase, sale));
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(List.of(saleReturn));

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        KardexReport report = result.getContent().get(0);
        assertTrue(report.isEntry());
        assertEquals(80, report.getTotalBalanceQuantity()); // 100 - 30 + 10
    }

    // ==================== Non-Commercial Movements ====================
    @Test
    @DisplayName("Should handle non-commercial entry as purchase")
    void testGetRecordsKardexByProduct_NonCommercialEntry_CreatesEntryReport() {
        // Given
        Kardex nonCommercialEntry = Kardex.createNonCommercialEntry(
            3001L,
            "Donation received",
            50,
            new BigDecimal("8.00"),
            mockProduct
        );
        nonCommercialEntry.setDate(ZonedDateTime.now());

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(new ArrayList<>());
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(List.of(nonCommercialEntry));

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        KardexReport report = result.getContent().get(0);
        assertTrue(report.isEntry());
        assertEquals(50, report.getTotalBalanceQuantity());
    }

    @Test
    @DisplayName("Should handle non-commercial exit as sale")
    void testGetRecordsKardexByProduct_NonCommercialExit_ConsumesFromQueue() {
        // Given
        Kardex purchase = Kardex.createPurchase(1001L, "Purchase", 100, new BigDecimal("10.00"), mockProduct);
        purchase.setDate(ZonedDateTime.now().minusDays(2));
        
        Kardex nonCommercialExit = Kardex.createNonCommercialExit(
            3002L,
            "Damage loss",
            25,
            new BigDecimal("10.00"),
            mockProduct
        );
        nonCommercialExit.setDate(ZonedDateTime.now().minusDays(1));

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(List.of(purchase));
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(List.of(nonCommercialExit));

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        KardexReport report = result.getContent().get(0);
        assertTrue(report.isOutput());
        assertEquals(75, report.getTotalBalanceQuantity()); // 100 - 25
    }

    // ==================== Pagination ====================
    @Test
    @DisplayName("Should paginate results correctly")
    void testGetRecordsKardexByProduct_Pagination_ReturnsCorrectPage() {
        // Given
        List<Kardex> movements = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            Kardex purchase = Kardex.createPurchase(
                1000L + i,
                "Purchase " + i,
                10,
                new BigDecimal("10.00"),
                mockProduct
            );
            purchase.setDate(ZonedDateTime.now().minusDays(26 - i));
            movements.add(purchase);
        }

        Pageable pageRequest = PageRequest.of(1, 10); // Second page, 10 items

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(new ArrayList<>());
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(movements);

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageRequest);

        // Then
        assertEquals(25, result.getTotalElements());
        assertEquals(3, result.getTotalPages()); // 25 / 10 = 3 pages
        assertEquals(10, result.getContent().size());
        assertEquals(1, result.getNumber()); // Current page
    }

    @Test
    @DisplayName("Should return empty page when page exceeds total")
    void testGetRecordsKardexByProduct_PageExceedsTotal_ReturnsEmptyPage() {
        // Given
        List<Kardex> movements = createPurchaseMovements();
        Pageable pageRequest = PageRequest.of(10, 10); // Page way beyond available data

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(new ArrayList<>());
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(movements);

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageRequest);

        // Then
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getNumberOfElements());
    }

    // ==================== Complex Scenarios ====================
    @Test
    @DisplayName("Should handle mixed movement types in correct order")
    void testGetRecordsKardexByProduct_MixedMovements_ProcessesInOrder() {
        // Given
        Kardex purchase1 = Kardex.createPurchase(1001L, "Purchase 1", 100, new BigDecimal("10.00"), mockProduct);
        purchase1.setDate(ZonedDateTime.now().minusDays(5));
        
        Kardex sale1 = Kardex.createSale(2001L, "Sale 1", 30, new BigDecimal("15.00"), mockProduct);
        sale1.setDate(ZonedDateTime.now().minusDays(4));
        
        Kardex purchase2 = Kardex.createPurchase(1002L, "Purchase 2", 50, new BigDecimal("12.00"), mockProduct);
        purchase2.setDate(ZonedDateTime.now().minusDays(3));
        
        Kardex sale2 = Kardex.createSale(2002L, "Sale 2", 40, new BigDecimal("15.00"), mockProduct);
        sale2.setDate(ZonedDateTime.now().minusDays(2));

        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(new ArrayList<>());
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(List.of(purchase1, sale1, purchase2, sale2));

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        assertEquals(4, result.getTotalElements());
        KardexReport lastReport = result.getContent().get(3);
        assertEquals(80, lastReport.getTotalBalanceQuantity()); // 100 - 30 + 50 - 40
    }

    @Test
    @DisplayName("Should handle empty movements list")
    void testGetRecordsKardexByProduct_EmptyMovements_ReturnsEmptyPage() {
        // Given
        given(kardexQueryOutputPort.findMovementsByProductBeforeDate(1L, startDate))
            .willReturn(new ArrayList<>());
        given(kardexQueryOutputPort.findMovementsByProductAndDateRange(1L, startDate, endDate))
            .willReturn(new ArrayList<>());

        // When
        Page<KardexReport> result = kardexQueryService.getRecordsKardexByProduct(1L, startDate, endDate, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    // ==================== Helper Methods ====================
    private List<Kardex> createPurchaseMovements() {
        Kardex purchase = Kardex.createPurchase(
            1001L,
            "Purchase",
            100,
            new BigDecimal("10.00"),
            mockProduct
        );
        purchase.setDate(ZonedDateTime.now());
        return List.of(purchase);
    }

    private List<Kardex> createSaleMovements() {
        Kardex sale = Kardex.createSale(
            2001L,
            "Sale",
            30,
            new BigDecimal("15.00"),
            mockProduct
        );
        sale.setDate(ZonedDateTime.now());
        return List.of(sale);
    }
}
