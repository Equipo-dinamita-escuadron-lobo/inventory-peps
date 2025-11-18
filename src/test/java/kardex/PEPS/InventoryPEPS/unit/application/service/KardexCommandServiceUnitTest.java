package kardex.PEPS.InventoryPEPS.unit.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import kardex.PEPS.InventoryPEPS.application.service.command.KardexCommandService ;
import kardex.PEPS.InventoryPEPS.domain.model.DetailOutput;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.command.IKardexCommandOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.query.IDetailQueryOutPutPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.query.IKardexQueryOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.query.IProductQueryOutputPort;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class KardexCommandServiceUnitTest {

    @Mock
    private  IKardexCommandOutputPort kardexCommandOutputPort;
    
    @Mock
    private IProductQueryOutputPort productQueryOutputPort;

    @Mock
    private IKardexQueryOutputPort kardexQueryOutputPort;
    
    @Mock
    private  IDetailQueryOutPutPort detailQueryOutPutPort;
    
    @Mock
    private  IFormatterResultOutputPort formatterResultOutputPort;

    @InjectMocks
    private KardexCommandService  kardexCommandService;

    private Product mockProduct;
    private Kardex kardexRequest;
    private Kardex purchaseLot;


   @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setProductId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setState(true);

        kardexRequest = new Kardex();
        kardexRequest.setProduct(mockProduct);
        kardexRequest.setFactCode(1001L);
        kardexRequest.setDetails("Test details");
        kardexRequest.setQuantity(100);
        kardexRequest.setUnitPrice(new BigDecimal("10.00"));

        purchaseLot = Kardex.createPurchase(
            1001L,
            "Purchase",
            200,
            new BigDecimal("10.00"),
            mockProduct
        );
        purchaseLot.setIdKardex(1L);
        purchaseLot.setDate(ZonedDateTime.now().minusDays(1));
    }

    // ==================== registerPurchase() ====================
    @Test
    @DisplayName("Should register purchase successfully")
    void testRegisterPurchase_ValidRequest_ReturnsPersistedKardex() {
        // Given
        Kardex expected = Kardex.createPurchase(
            1001L,
            "Purchase",
            100,
            new BigDecimal("10.00"),
            mockProduct
        );
        
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.of(mockProduct));
        given(kardexCommandOutputPort.registerPurchase(any(Kardex.class)))
            .willReturn(expected);

        // When
        Kardex result = kardexCommandService.registerPurchase(kardexRequest);

        // Then
        assertNotNull(result);
        assertEquals(1001L, result.getFactCode());
        verify(productQueryOutputPort).getProductByProductId(1L);
        verify(kardexCommandOutputPort).registerPurchase(any(Kardex.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found for purchase")
    void testRegisterPurchase_ProductNotFound_ThrowsException() {
        // Given
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> kardexCommandService.registerPurchase(kardexRequest)
        );

        assertEquals("Product not found", exception.getMessage());
        verify(formatterResultOutputPort)
            .returnEntityDoesNotExistErrorResponse(404, "Product not found");
    }

    // ==================== registerSale() ====================
    @Test
    @DisplayName("Should register sale with FIFO processing successfully")
    void testRegisterSale_ValidRequest_ExecutesFIFOAndRegistersSale() {
        // Given
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.of(mockProduct));
        given(kardexQueryOutputPort.findAvailablePurchasesOrderedByDate(1L))
            .willReturn(List.of(purchaseLot));
        given(kardexCommandOutputPort.registerSale(any(Kardex.class), anyList()))
            .willAnswer(invocation -> invocation.getArgument(0));

        // When
        Kardex result = kardexCommandService.registerSale(kardexRequest);

        // Then
        assertNotNull(result);
        assertEquals(1001L, result.getFactCode());
        assertEquals(100, result.getQuantity());
        verify(kardexQueryOutputPort).findAvailablePurchasesOrderedByDate(1L);
        verify(kardexCommandOutputPort).registerSale(any(Kardex.class), anyList());
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock for sale")
    void testRegisterSale_InsufficientStock_ThrowsException() {
        // Given
        Kardex limitedLot = Kardex.createPurchase(
            1002L,
            "Limited Purchase",
            50,
            new BigDecimal("10.00"),
            mockProduct
        );
        
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.of(mockProduct));
        given(kardexQueryOutputPort.findAvailablePurchasesOrderedByDate(1L))
            .willReturn(List.of(limitedLot));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> kardexCommandService.registerSale(kardexRequest)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(formatterResultOutputPort)
            .returnBusinessRuleErrorResponse(eq(400), contains("Insufficient stock"));
    }

    @Test
    @DisplayName("Should process FIFO from multiple lots")
    void testRegisterSale_MultipleLots_ProcessesFIFOCorrectly() {
        // Given
        Kardex lot1 = Kardex.createPurchase(1003L, "Lot 1", 60, new BigDecimal("10.00"), mockProduct);
        lot1.setIdKardex(2L);
        lot1.setDate(ZonedDateTime.now().minusDays(2));
        
        Kardex lot2 = Kardex.createPurchase(1004L, "Lot 2", 80, new BigDecimal("12.00"), mockProduct);
        lot2.setIdKardex(3L);
        lot2.setDate(ZonedDateTime.now().minusDays(1));
        
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.of(mockProduct));
        given(kardexQueryOutputPort.findAvailablePurchasesOrderedByDate(1L))
            .willReturn(List.of(lot1, lot2));
        given(kardexCommandOutputPort.registerSale(any(Kardex.class), anyList()))
            .willAnswer(invocation -> invocation.getArgument(0));

        // When
        Kardex result = kardexCommandService.registerSale(kardexRequest);

        // Then
        assertNotNull(result);
        // Verify FIFO logic reduced quantities
        assertEquals(0, lot1.getAvailableQuantity()); // Fully consumed
        assertEquals(40, lot2.getAvailableQuantity()); // Partially consumed
        
        ArgumentCaptor<List> lotsCaptor = ArgumentCaptor.forClass(List.class);
        verify(kardexCommandOutputPort).registerSale(any(Kardex.class), lotsCaptor.capture());
        assertEquals(2, lotsCaptor.getValue().size());
    }

    // ==================== registerPurchaseReturn() ====================
    @Test
    @DisplayName("Should register purchase return successfully")
    void testRegisterPurchaseReturn_ValidRequest_ReturnsRegisteredReturn() {
        // Given
        kardexRequest.setQuantity(30);
        
        given(kardexQueryOutputPort.findByRefFacture(1001L, 1L))
            .willReturn(Optional.of(purchaseLot));
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.of(mockProduct));
        given(kardexCommandOutputPort.registerPurchaseReturn(any(Kardex.class)))
            .willAnswer(invocation -> invocation.getArgument(0));

        // When
        Kardex result = kardexCommandService.registerPurchaseReturn(kardexRequest);

        // Then
        assertNotNull(result);
        assertEquals(1001L, result.getFactCode());
        verify(kardexCommandOutputPort)
            .updateAvaliableAmount(1L, 170); // 200 - 30
        verify(kardexCommandOutputPort).registerPurchaseReturn(any(Kardex.class));
    }

    @Test
    @DisplayName("Should throw exception when original purchase not found")
    void testRegisterPurchaseReturn_PurchaseNotFound_ThrowsException() {
        // Given
        given(kardexQueryOutputPort.findByRefFacture(1001L, 1L))
            .willReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> kardexCommandService.registerPurchaseReturn(kardexRequest)
        );

        assertEquals("Original purchase not found", exception.getMessage());
        verify(formatterResultOutputPort)
            .returnEntityDoesNotExistErrorResponse(404, "Original purchase not found");
    }

    @Test
    @DisplayName("Should throw exception when return validation fails")
    void testRegisterPurchaseReturn_ValidationFails_ThrowsException() {
        // Given
        kardexRequest.setQuantity(300); // More than purchased
        
        given(kardexQueryOutputPort.findByRefFacture(1001L, 1L))
            .willReturn(Optional.of(purchaseLot));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> kardexCommandService.registerPurchaseReturn(kardexRequest)
        );

        assertTrue(exception.getMessage().contains("Cannot return more than purchased"));
        verify(formatterResultOutputPort)
            .returnBusinessRuleErrorResponse(eq(400), anyString());
    }

    @Test
    @DisplayName("Should register sale return with LIFO processing")
    void testRegisterSaleReturn_ValidRequest_ProcessesLIFOAndReturns() {
        // Given
        Kardex saleMovement = Kardex.createSale(2001L, "Sale", 50, new BigDecimal("10.00"), mockProduct);
        saleMovement.setIdKardex(10L);
        saleMovement.setDate(ZonedDateTime.now());
        
        kardexRequest.setFactCode(2001L);
        kardexRequest.setQuantity(50);
        
       
        purchaseLot.setAvailableQuantity(150);
        
        DetailOutput detail1 = DetailOutput.create(30, new BigDecimal("10.00"), saleMovement, purchaseLot);
        detail1.setIdDetailOutput(1L);
        
        DetailOutput detail2 = DetailOutput.create(20, new BigDecimal("12.00"), saleMovement, purchaseLot);
        detail2.setIdDetailOutput(2L);
        
        given(kardexQueryOutputPort.findByRefFacture(2001L, 1L))
            .willReturn(Optional.of(saleMovement));
        given(detailQueryOutPutPort.findByMovementSaleOrderedDesc(10L))
            .willReturn(List.of(detail2, detail1)); // LIFO order
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.of(mockProduct));
        
        given(kardexCommandOutputPort.registerSaleReturn(any(Kardex.class)))
            .willAnswer(invocation -> invocation.getArgument(0));
       doNothing().when(detailQueryOutPutPort).deleteById(anyLong());

        // When
        List<Kardex> results = kardexCommandService.registerSaleReturn(kardexRequest);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(detailQueryOutPutPort, times(2)).deleteById(anyLong());
        verify(kardexCommandOutputPort, times(2)).updateAvaliableAmount(anyLong(), anyInt());
    }


    @Test
    @DisplayName("Should throw exception when sale not found for return")
    void testRegisterSaleReturn_SaleNotFound_ThrowsException() {
        // Given
        given(kardexQueryOutputPort.findByRefFacture(1001L, 1L))
            .willReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> kardexCommandService.registerSaleReturn(kardexRequest)
        );

        assertEquals("Sale to be returned not found", exception.getMessage());
        verify(formatterResultOutputPort)
            .returnBusinessRuleErrorResponse(400, "Sale to be returned not found");
    }

    @Test
    @DisplayName("Should throw exception when sale has no details")
    void testRegisterSaleReturn_NoDetails_ThrowsException() {
        // Given
        // ✅ CORRECCIÓN: La cantidad a devolver debe ser <= cantidad vendida
        Kardex saleMovement = Kardex.createSale(2001L, "Sale", 50, new BigDecimal("10.00"), mockProduct);
        saleMovement.setIdKardex(10L);
        
        kardexRequest.setFactCode(2001L);
        kardexRequest.setQuantity(50); // ✅ Cambiar de 100 a 50 o menos
        
        given(kardexQueryOutputPort.findByRefFacture(2001L, 1L))
            .willReturn(Optional.of(saleMovement));
        given(detailQueryOutPutPort.findByMovementSaleOrderedDesc(10L))
            .willReturn(new ArrayList<>());

        // When
        List<Kardex> results = kardexCommandService.registerSaleReturn(kardexRequest);

        // Then
        assertTrue(results.isEmpty());
        verify(formatterResultOutputPort)
            .returnBusinessRuleErrorResponse(400, "Sale has no details to return");
    }

    @Test
    @DisplayName("Should process partial sale return")
    void testRegisterSaleReturn_PartialReturn_UpdatesDetailCorrectly() {
        // Given
        Kardex saleMovement = Kardex.createSale(2001L, "Sale", 50, new BigDecimal("10.00"), mockProduct);
        saleMovement.setIdKardex(10L);
        
        kardexRequest.setFactCode(2001L);
        kardexRequest.setQuantity(15);
        
        purchaseLot.setAvailableQuantity(170);
        
        DetailOutput detail = DetailOutput.create(30, new BigDecimal("10.00"), saleMovement, purchaseLot);
        detail.setIdDetailOutput(1L);
        
        when(kardexQueryOutputPort.findByRefFacture(2001L, 1L))
            .thenReturn(Optional.of(saleMovement));
        when(detailQueryOutPutPort.findByMovementSaleOrderedDesc(10L))
            .thenReturn(List.of(detail));
        when(productQueryOutputPort.getProductByProductId(1L))
            .thenReturn(Optional.of(mockProduct));
        
        when(kardexCommandOutputPort.updateAvaliableAmount(anyLong(), anyInt()))
            .thenReturn(1);
        
        // ✅ CORRECCIÓN: update() retorna DetailOutput
        when(detailQueryOutPutPort.update(any(DetailOutput.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        when(kardexCommandOutputPort.registerSaleReturn(any(Kardex.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<Kardex> results = kardexCommandService.registerSaleReturn(kardexRequest);

        // Then
        assertEquals(1, results.size());
        verify(detailQueryOutPutPort).update(any(DetailOutput.class));
        verify(detailQueryOutPutPort, never()).deleteById(anyLong());
    }




    // ==================== registerNonCommercialExit() ====================
    @Test
    @DisplayName("Should register non-commercial exit successfully")
    void testRegisterNonCommercialExit_ValidRequest_RegistersExit() {
        // Given
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.of(mockProduct));
        given(kardexQueryOutputPort.findAvailablePurchasesOrderedByDate(1L))
            .willReturn(List.of(purchaseLot));
        given(kardexCommandOutputPort.registerNonCommercialExit(any(Kardex.class), anyList()))
            .willAnswer(invocation -> invocation.getArgument(0));

        // When
        Kardex result = kardexCommandService.registerNonCommercialExit(kardexRequest);

        // Then
        assertNotNull(result);
        assertEquals(1001L, result.getFactCode());
        verify(kardexQueryOutputPort).findAvailablePurchasesOrderedByDate(1L);
        verify(kardexCommandOutputPort).registerNonCommercialExit(any(Kardex.class), anyList());
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock for non-commercial exit")
    void testRegisterNonCommercialExit_InsufficientStock_ThrowsException() {
        // Given
        Kardex limitedLot = Kardex.createPurchase(1002L, "Limited", 50, new BigDecimal("10.00"), mockProduct);
        
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.of(mockProduct));
        given(kardexQueryOutputPort.findAvailablePurchasesOrderedByDate(1L))
            .willReturn(List.of(limitedLot));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> kardexCommandService.registerNonCommercialExit(kardexRequest)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    // ==================== registerNonCommercialEntry() ====================
    @Test
    @DisplayName("Should register non-commercial entry successfully")
    void testRegisterNonCommercialEntry_ValidRequest_RegistersEntry() {
        // Given
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.of(mockProduct));
        given(kardexCommandOutputPort.registerNonCommercialEntry(any(Kardex.class)))
            .willAnswer(invocation -> invocation.getArgument(0));

        // When
        Kardex result = kardexCommandService.registerNonCommercialEntry(kardexRequest);

        // Then
        assertNotNull(result);
        assertEquals(1001L, result.getFactCode());
        verify(kardexCommandOutputPort).registerNonCommercialEntry(any(Kardex.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found for non-commercial entry")
    void testRegisterNonCommercialEntry_ProductNotFound_ThrowsException() {
        // Given
        given(productQueryOutputPort.getProductByProductId(1L))
            .willReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> kardexCommandService.registerNonCommercialEntry(kardexRequest)
        );

        assertEquals("Product not found", exception.getMessage());
    }







}
