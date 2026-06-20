package kardex.PEPS.InventoryPEPS.unit.infrastucture.output.remoteSync.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.adapter.ProductClientAdapter;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IProductClient;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.ProductSyncDto;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.mapper.IProductClientMapper;

@ExtendWith(MockitoExtension.class)
public class ProductClientAdapterUnitTest {
     @Mock
    private IProductClientMapper productClientMapper;

    @Mock
    private IProductClient productClient;

    @InjectMocks
    private ProductClientAdapter productClientAdapter;

    private String enterpriseId;
    private Instant since;
    private List<ProductSyncDto> mockSyncDtos;
    private List<Product> mockProducts;

    @BeforeEach
    void setUp() {
        enterpriseId = "ENT-001";
        since = Instant.parse("2025-01-01T00:00:00Z");
        
        // Initialize mock Sync DTOs
        mockSyncDtos = createMockSyncDtos();
        
        // Initialize mock domain Products
        mockProducts = createMockProducts();
    }

    // ==================== findAllProductsByEnterpriseId() ====================
    @Test
    @DisplayName("Should fetch and map products successfully")
    void testFindAllProductsByEnterpriseId_ValidParameters_ReturnsProducts() {
        // Arrange
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, since))
            .thenReturn(mockSyncDtos);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenAnswer(invocation -> {
                ProductSyncDto dto = invocation.getArgument(0);
                Product product = new Product();
                product.setProductId(dto.getProductId());
                product.setName(dto.getName());
                return product;
            });

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, since);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(productClient, times(1)).findAllProductsByEnterpriseId(enterpriseId, since);
        verify(productClientMapper, times(3)).toDomain(any(ProductSyncDto.class));
    }

    @Test
    @DisplayName("Should return empty list when no products found")
    void testFindAllProductsByEnterpriseId_NoProducts_ReturnsEmptyList() {
        // Arrange
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, since))
            .thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, since);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productClient, times(1)).findAllProductsByEnterpriseId(enterpriseId, since);
        verify(productClientMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should handle single product correctly")
    void testFindAllProductsByEnterpriseId_SingleProduct_ReturnsSingleProductList() {
        // Arrange
        List<ProductSyncDto> singleDto = List.of(createProductSyncDto(1L, "Product 1"));
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, since))
            .thenReturn(singleDto);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenReturn(createProduct(1L, "Product 1"));

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, since);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getProductId());
        verify(productClientMapper, times(1)).toDomain(any(ProductSyncDto.class));
    }

    @Test
    @DisplayName("Should map all products correctly")
    void testFindAllProductsByEnterpriseId_MultipleProducts_MapsAllCorrectly() {
        // Arrange
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, since))
            .thenReturn(mockSyncDtos);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenAnswer(invocation -> {
                ProductSyncDto dto = invocation.getArgument(0);
                return createProduct(dto.getProductId(), dto.getName());
            });

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, since);

        // Assert
        assertEquals(3, result.size());
        assertEquals(100L, result.get(0).getProductId());
        assertEquals(101L, result.get(1).getProductId());
        assertEquals(102L, result.get(2).getProductId());
    }

    @Test
    @DisplayName("Should handle null enterpriseId")
    void testFindAllProductsByEnterpriseId_NullEnterpriseId_CallsClient() {
        // Arrange
        when(productClient.findAllProductsByEnterpriseId(null, since))
            .thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(null, since);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productClient, times(1)).findAllProductsByEnterpriseId(null, since);
    }

    @Test
    @DisplayName("Should handle null since date")
    void testFindAllProductsByEnterpriseId_NullSince_CallsClient() {
        // Arrange
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, null))
            .thenReturn(mockSyncDtos);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenReturn(createProduct(100L, "Product"));

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, null);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(productClient, times(1)).findAllProductsByEnterpriseId(enterpriseId, null);
    }

    @Test
    @DisplayName("Should handle large list of products")
    void testFindAllProductsByEnterpriseId_LargeList_ProcessesSuccessfully() {
        // Arrange
        List<ProductSyncDto> largeList = new ArrayList<>();
        for (long i = 1; i <= 1000; i++) {
            largeList.add(createProductSyncDto(i, "Product " + i));
        }
        
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, since))
            .thenReturn(largeList);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenAnswer(invocation -> {
                ProductSyncDto dto = invocation.getArgument(0);
                return createProduct(dto.getProductId(), dto.getName());
            });

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, since);

        // Assert
        assertEquals(1000, result.size());
        verify(productClient, times(1)).findAllProductsByEnterpriseId(enterpriseId, since);
        verify(productClientMapper, times(1000)).toDomain(any(ProductSyncDto.class));
    }

    @Test
    @DisplayName("Should handle different enterprise IDs")
    void testFindAllProductsByEnterpriseId_DifferentEnterpriseIds_FetchesCorrectData() {
        // Arrange
        String enterpriseId2 = "ENT-002";
        when(productClient.findAllProductsByEnterpriseId(enterpriseId2, since))
            .thenReturn(mockSyncDtos);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenReturn(createProduct(100L, "Product"));

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId2, since);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(productClient, times(1)).findAllProductsByEnterpriseId(enterpriseId2, since);
    }

    @Test
    @DisplayName("Should handle different since dates")
    void testFindAllProductsByEnterpriseId_DifferentSinceDates_FetchesCorrectData() {
        // Arrange
        Instant differentSince = Instant.parse("2024-01-01T00:00:00Z");
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, differentSince))
            .thenReturn(mockSyncDtos);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenReturn(createProduct(100L, "Product"));

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, differentSince);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(productClient, times(1)).findAllProductsByEnterpriseId(enterpriseId, differentSince);
    }

    // ==================== Error Handling ====================
    @Test
    @DisplayName("Should propagate exception when client throws exception")
    void testFindAllProductsByEnterpriseId_ClientThrowsException_PropagatesException() {
        // Arrange
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, since))
            .thenThrow(new RuntimeException("Connection timeout"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, since)
        );
        
        verify(productClient, times(1)).findAllProductsByEnterpriseId(enterpriseId, since);
        verify(productClientMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should propagate exception when mapper throws exception")
    void testFindAllProductsByEnterpriseId_MapperThrowsException_PropagatesException() {
        // Arrange
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, since))
            .thenReturn(mockSyncDtos);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenThrow(new RuntimeException("Mapping error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, since)
        );
        
        verify(productClient, times(1)).findAllProductsByEnterpriseId(enterpriseId, since);
    }

    // ==================== Integration Scenarios ====================
    @Test
    @DisplayName("Should maintain order of products from client")
    void testFindAllProductsByEnterpriseId_MaintainsOrder_ReturnsInSameOrder() {
        // Arrange
        List<ProductSyncDto> orderedDtos = List.of(
            createProductSyncDto(3L, "Product C"),
            createProductSyncDto(1L, "Product A"),
            createProductSyncDto(2L, "Product B")
        );
        
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, since))
            .thenReturn(orderedDtos);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenAnswer(invocation -> {
                ProductSyncDto dto = invocation.getArgument(0);
                return createProduct(dto.getProductId(), dto.getName());
            });

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, since);

        // Assert
        assertEquals(3, result.size());
        assertEquals(3L, result.get(0).getProductId());
        assertEquals(1L, result.get(1).getProductId());
        assertEquals(2L, result.get(2).getProductId());
    }

    @Test
    @DisplayName("Should return immutable list")
    void testFindAllProductsByEnterpriseId_ReturnsImmutableList() {
        // Arrange
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, since))
            .thenReturn(mockSyncDtos);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenReturn(createProduct(100L, "Product"));

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, since);

        // Assert
        assertNotNull(result);
        assertThrows(UnsupportedOperationException.class, () -> 
            result.add(createProduct(999L, "New Product"))
        );
    }

    @Test
    @DisplayName("Should handle products with different states")
    void testFindAllProductsByEnterpriseId_MixedStates_ProcessesAll() {
        // Arrange
        List<ProductSyncDto> mixedDtos = new ArrayList<>();
        mixedDtos.add(createProductSyncDto(1L, "Active Product", true));
        mixedDtos.add(createProductSyncDto(2L, "Inactive Product", false));
        
        when(productClient.findAllProductsByEnterpriseId(enterpriseId, since))
            .thenReturn(mixedDtos);
        when(productClientMapper.toDomain(any(ProductSyncDto.class)))
            .thenAnswer(invocation -> {
                ProductSyncDto dto = invocation.getArgument(0);
                Product p = createProduct(dto.getProductId(), dto.getName());
                p.setState(dto.isState());
                return p;
            });

        // Act
        List<Product> result = productClientAdapter.findAllProductsByEnterpriseId(enterpriseId, since);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.get(0).isActive());
        assertFalse(result.get(1).isActive());
    }

    // ==================== Helper Methods ====================
    private ProductSyncDto createProductSyncDto(Long productId, String name) {
        return createProductSyncDto(productId, name, true);
    }

    private ProductSyncDto createProductSyncDto(Long productId, String name, boolean state) {
        ProductSyncDto dto = new ProductSyncDto();
        dto.setProductId(productId);
        dto.setName(name);
        dto.setReference("REF-" + productId);
        dto.setEnterpriseId(enterpriseId);
        dto.setPresentation("Box");
        dto.setState(state);
        return dto;
    }

    private Product createProduct(Long productId, String name) {
        Product product = new Product();
        product.setProductId(productId);
        product.setName(name);
        product.setReference("REF-" + productId);
        product.setEnterpriseId(enterpriseId);
        product.setPresentation("Box");
        product.setState(true);
        return product;
    }

    private List<ProductSyncDto> createMockSyncDtos() {
        List<ProductSyncDto> dtos = new ArrayList<>();
        for (long i = 100; i <= 102; i++) {
            dtos.add(createProductSyncDto(i, "Product " + i));
        }
        return dtos;
    }

    private List<Product> createMockProducts() {
        List<Product> products = new ArrayList<>();
        for (long i = 100; i <= 102; i++) {
            products.add(createProduct(i, "Product " + i));
        }
        return products;
    }

}
