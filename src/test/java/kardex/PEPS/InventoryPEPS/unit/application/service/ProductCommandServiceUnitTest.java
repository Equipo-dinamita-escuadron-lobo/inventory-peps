package kardex.PEPS.InventoryPEPS.unit.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import kardex.PEPS.InventoryPEPS.application.service.command.ProductCommandService;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.model.SyncState;
import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IMessageServicePort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IProductClientPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IProductCommandOutPutPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.ISyncStateRepositoryPort;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductCommandServiceUnitTest {
    
    @Mock
    private IProductCommandOutPutPort productCommandOutPutPort;

    @Mock
    private IProductClientPort productClient;

    @Mock
    private ISyncStateRepositoryPort syncStateRepository;

    @Mock
    private IFormatterResultOutputPort formatterResultOutputPort;

    @Mock
    private IMessageServicePort messageService;

    @InjectMocks
    private ProductCommandService productCommandService;

    private String enterpriseId;
    private SyncState mockSyncState;
    private List<Product> mockProducts;

     @BeforeEach
    void setUp() {
        // Arrange - Setup común
        enterpriseId = "ENT-001";
        
        mockSyncState = SyncState.createInitialProductSync(enterpriseId);
        mockSyncState.setId(1L);
        
        mockProducts = createMockProducts();
        
            
        when(messageService.getMessage(anyString())).thenReturn("Mocked message");
        when(messageService.getMessage(anyString(), any())).thenReturn("Mocked message");
        when(messageService.getMessage(anyString(), any(), any())).thenReturn("Mocked message");
        
       
        when(messageService.getMessage(anyString(), anyString())).thenReturn("Error message");
        when(messageService.getMessage(anyString(), anyString(), anyString())).thenReturn("Error message with details");
    }

   
    @Test
    @DisplayName("Should perform first sync successfully when no previous sync state exists")
    void testSyncProducts_FirstSync_Success() {
        // Arrange
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.empty());
        // ✅ CORRECCIÓN: save() es void - no usar thenReturn ni thenAnswer
        doNothing().when(syncStateRepository).save(any(SyncState.class));
        
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(mockProducts);
        when(productCommandOutPutPort.saveAll(anyList()))
            .thenReturn("3 products saved");

        // Act
        String result = productCommandService.syncProductsByEnterpriseId(enterpriseId);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Successful synchronization"));
        verify(syncStateRepository, times(2)).save(any(SyncState.class)); // Create + Update
        verify(productCommandOutPutPort).saveAll(anyList());
    }

    @Test
    @DisplayName("Should use existing sync state when available")
    void testSyncProducts_ExistingSyncState_Success() {
        // Arrange
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(mockSyncState));

        doNothing().when(syncStateRepository).save(any(SyncState.class));
        
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(mockProducts);
        when(productCommandOutPutPort.saveAll(anyList()))
            .thenReturn("3 products saved");

        // Act
        String result = productCommandService.syncProductsByEnterpriseId(enterpriseId);

        // Assert
        assertNotNull(result);
        verify(syncStateRepository, times(2)).save(any(SyncState.class));
        verify(productClient).findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class));
    }

    
    @Test
    @DisplayName("Should reset sync state when date consistency validation fails")
    void testSyncProducts_InconsistentSyncState_ResetsState() {
        // Arrange
        Instant now = Instant.now();
        
        // Estado con inconsistencia REAL
        SyncState inconsistentState = SyncState.builder()
            .id(1L)
            .syncType("products")
            .enterpriseId(enterpriseId)
            .createdAt(now)
            .updatedAt(now.minusSeconds(3600)) // Antes de createdAt
            .lastSyncDate(now.minusSeconds(7200)) // Antes de createdAt
            .build();
        
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(inconsistentState));
        doNothing().when(syncStateRepository).save(any(SyncState.class));
        
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(new ArrayList<>());

        // Act
        String result = productCommandService.syncProductsByEnterpriseId(enterpriseId);

        // Assert
       
        verify(syncStateRepository, atLeast(2)).save(any(SyncState.class));
        assertNotNull(result);
        assertTrue(result.contains("No products to process") || 
                result.contains("Synchronization completed"));
    }



   @Test
    @DisplayName("Should update sync state with new sync date after successful sync")
    void testSyncProducts_Success_UpdatesSyncState() {
        // Arrange
        Instant beforeSync = Instant.now().minusSeconds(1);
        
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(mockSyncState));
        doNothing().when(syncStateRepository).save(any(SyncState.class));
        
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(mockProducts);
        when(productCommandOutPutPort.saveAll(anyList()))
            .thenReturn("3 products saved");

        // Act
        productCommandService.syncProductsByEnterpriseId(enterpriseId);

        // Assert
        ArgumentCaptor<SyncState> stateCaptor = ArgumentCaptor.forClass(SyncState.class);
        // ✅ CORRECCIÓN: Acepta 2 llamadas (reset + update)
        verify(syncStateRepository, times(2)).save(stateCaptor.capture());
        
        // Verifica la ÚLTIMA actualización
        SyncState updatedState = stateCaptor.getValue();
        assertTrue(updatedState.getLastSyncDate().isAfter(beforeSync));
    }



    @Test
    @DisplayName("Should process and save all valid products")
    void testSyncProducts_ValidProducts_SavesAll() {
        // Arrange
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(mockSyncState));
        doNothing().when(syncStateRepository).save(any(SyncState.class));
        
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(mockProducts);
        when(productCommandOutPutPort.saveAll(anyList()))
            .thenReturn("3 products saved");

        // Act
        String result = productCommandService.syncProductsByEnterpriseId(enterpriseId);

        // Assert
        ArgumentCaptor<List<Product>> productsCaptor = ArgumentCaptor.forClass(List.class);
        verify(productCommandOutPutPort).saveAll(productsCaptor.capture());
        
        List<Product> savedProducts = productsCaptor.getValue();
        assertEquals(3, savedProducts.size());
        
        // ✅ CORRECCIÓN: Verifica que el resultado no sea null y contenga información relevante
        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("success") || 
                result.toLowerCase().contains("synchronization") ||
                result.contains("3"));
    }


    @Test
    @DisplayName("Should handle empty product list from client")
    void testSyncProducts_NoProducts_ReturnsNoProductsMessage() {
        // Arrange
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(mockSyncState));
        // ✅ CORRECCIÓN: void method
        doNothing().when(syncStateRepository).save(any(SyncState.class));
        
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(new ArrayList<>());

        // Act
        String result = productCommandService.syncProductsByEnterpriseId(enterpriseId);

        // Assert
        assertTrue(result.contains("No products to process"));
        verify(productCommandOutPutPort, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Should filter out invalid products during conversion")
    void testSyncProducts_InvalidProducts_FiltersThemOut() {
        // Arrange
        List<Product> mixedProducts = new ArrayList<>();
        
        Product validProduct = new Product();
        validProduct.setProductId(1L);
        validProduct.setName("Valid Product");
        validProduct.setReference("REF-001");
        validProduct.setPresentation("Box");
        validProduct.setState(true);
        mixedProducts.add(validProduct);
        
        Product invalidProduct = new Product();
        invalidProduct.setProductId(2L);
        invalidProduct.setName(null);
        invalidProduct.setReference("REF-002");
        mixedProducts.add(invalidProduct);
        
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(mockSyncState));
        // ✅ CORRECCIÓN: void method
        doNothing().when(syncStateRepository).save(any(SyncState.class));
        
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(mixedProducts);
        when(productCommandOutPutPort.saveAll(anyList()))
            .thenReturn("1 product saved");

        // Act
        String result = productCommandService.syncProductsByEnterpriseId(enterpriseId);

        // Assert
        ArgumentCaptor<List<Product>> productsCaptor = ArgumentCaptor.forClass(List.class);
        verify(productCommandOutPutPort).saveAll(productsCaptor.capture());
        
        assertEquals(1, productsCaptor.getValue().size());
        assertTrue(result.contains("1"));
    }

    @Test
    @DisplayName("Should handle inactive products correctly")
    void testSyncProducts_InactiveProducts_DeactivatesThem() {
        // Arrange
        List<Product> products = new ArrayList<>();
        Product inactiveProduct = new Product();
        inactiveProduct.setProductId(1L);
        inactiveProduct.setName("Inactive Product");
        inactiveProduct.setReference("REF-001");
        inactiveProduct.setPresentation("Box");
        inactiveProduct.setState(false);
        products.add(inactiveProduct);
        
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(mockSyncState));
        // ✅ CORRECCIÓN: void method
        doNothing().when(syncStateRepository).save(any(SyncState.class));
        
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(products);
        when(productCommandOutPutPort.saveAll(anyList()))
            .thenReturn("1 product saved");

        // Act
        productCommandService.syncProductsByEnterpriseId(enterpriseId);

        // Assert
        ArgumentCaptor<List<Product>> productsCaptor = ArgumentCaptor.forClass(List.class);
        verify(productCommandOutPutPort).saveAll(productsCaptor.capture());
        
        Product savedProduct = productsCaptor.getValue().get(0);
        assertFalse(savedProduct.isActive());
    }

    // ==================== Error Handling ====================
    @Test
    @DisplayName("Should throw exception when client fails to fetch products")
    void testSyncProducts_ClientError_ThrowsException() {
        // Arrange
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(mockSyncState));
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenThrow(new RuntimeException("Client connection failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> productCommandService.syncProductsByEnterpriseId(enterpriseId));
        
        verify(formatterResultOutputPort).returnBusinessRuleErrorResponse(eq(500), anyString());
    }

    @Test
    @DisplayName("Should throw exception when product save fails")
    void testSyncProducts_SaveError_ThrowsException() {
        // Arrange
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(mockSyncState));
        // ✅ CORRECCIÓN: void method
        doNothing().when(syncStateRepository).save(any(SyncState.class));
        
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(mockProducts);
        when(productCommandOutPutPort.saveAll(anyList()))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> productCommandService.syncProductsByEnterpriseId(enterpriseId));
        
        verify(formatterResultOutputPort).returnBusinessRuleErrorResponse(eq(500), anyString());
    }

    @Test
    @DisplayName("Should throw exception when sync state save fails")
    void testSyncProducts_SyncStateSaveError_ThrowsException() {
        // Arrange
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(mockSyncState));
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(mockProducts);
        when(productCommandOutPutPort.saveAll(anyList()))
            .thenReturn("3 products saved");
        // ✅ CORRECCIÓN: Para lanzar excepción en void method usa doThrow
        doThrow(new RuntimeException("Database error"))
            .when(syncStateRepository).save(any(SyncState.class));

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> productCommandService.syncProductsByEnterpriseId(enterpriseId));
    }

    // ==================== Result Formatting ====================
    @Test
    @DisplayName("Should format message correctly for new products only")
    void testSyncProducts_NewProductsOnly_FormatsMessageCorrectly() {
        // Arrange
        List<Product> newProducts = createNewProducts();
        
        when(syncStateRepository.findBySyncTypeAndEnterpriseId("products", enterpriseId))
            .thenReturn(Optional.of(mockSyncState));
        // ✅ CORRECCIÓN: void method
        doNothing().when(syncStateRepository).save(any(SyncState.class));
        
        when(productClient.findAllProductsByEnterpriseId(eq(enterpriseId), any(Instant.class)))
            .thenReturn(newProducts);
        when(productCommandOutPutPort.saveAll(anyList()))
            .thenReturn("2 products saved");

        // Act
        String result = productCommandService.syncProductsByEnterpriseId(enterpriseId);

        // Assert
        assertTrue(result.contains("new products") || result.contains("products processed"));
    }

    // ==================== Helper Methods ====================
    private List<Product> createMockProducts() {
        List<Product> products = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            Product product = new Product();
            product.setProductId((long) i);
            product.setName("Product " + i);
            product.setReference("REF-00" + i);
            product.setPresentation("Box");
            product.setState(true);
            products.add(product);
        }
        
        return products;
    }
    
    private List<Product> createNewProducts() {
        List<Product> products = new ArrayList<>();
        
        for (int i = 1; i <= 2; i++) {
            Product product = new Product();
            product.setProductId((long) i);
            product.setName("New Product " + i);
            product.setReference("NEW-00" + i);
            product.setPresentation("Box");
            product.setState(true);
            products.add(product);
        }
        
        return products;
    }
    
}
