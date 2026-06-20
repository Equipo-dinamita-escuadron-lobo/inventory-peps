package kardex.PEPS.InventoryPEPS.unit.infrastucture.output.remoteSync.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import kardex.PEPS.InventoryPEPS.domain.model.Stock;
import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.exception.customized.GenericErrorException;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.adapter.StockClientAdapter;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IStockClient;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockBuyDtoRequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockDtoResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockSellDtoRequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.mapper.IStockClientMapper;

@ExtendWith(MockitoExtension.class)
public class StockClientAdapterUnitTest {
    @Mock
    private IStockClient stockClient;

    @Mock
    private IStockClientMapper stockClientMapper;
    
    @Mock
    private IFormatterResultOutputPort formatterResultOutputPort;

    @InjectMocks
    private StockClientAdapter stockClientAdapter;

    private Stock mockStock;
    private StockBuyDtoRequest mockBuyRequest;
    private StockSellDtoRequest mockSellRequest;
    private StockDtoResponse mockResponse;
    private ResponseDTO<StockDtoResponse> mockResponseDTO;

    @BeforeEach
    void setUp() {
        // Initialize Stock domain object
        mockStock = new Stock();
        mockStock.setProductId(100L);
        mockStock.setQuantity(50);
        mockStock.setPrice(BigDecimal.valueOf(25.50));
        mockStock.setEnterpriseId("ENT-001");

        // Initialize Buy DTO Request
        mockBuyRequest = StockBuyDtoRequest.builder()
            .productId(100L)
            .quantity(50)
            .price(BigDecimal.valueOf(25.50))
            .build();

        // Initialize Sell DTO Request
        mockSellRequest = StockSellDtoRequest.builder()
            .productId(100L)
            .quantity(50)
            .price(BigDecimal.valueOf(25.50))
            .build();

        // Initialize Response DTO
        mockResponse = new StockDtoResponse();
        mockResponse.setId(1L);
        mockResponse.setProductId(100L);
        mockResponse.setEnterpriseId("ENT-001");
        mockResponse.setQuantity(50);
        mockResponse.setPrice(BigDecimal.valueOf(25.50));
        mockResponse.setStatus(true);

        // Initialize ResponseDTO wrapper
        mockResponseDTO = new ResponseDTO<>();
        mockResponseDTO.setData(mockResponse);
    }

    // ==================== buyStock() ====================
    @Test
    @DisplayName("Should buy stock successfully")
    void testBuyStock_Success_ReturnsNormally() {
        // Arrange
        when(stockClientMapper.toDtoRequest(mockStock)).thenReturn(mockBuyRequest);
        when(stockClient.buyStock(mockBuyRequest))
            .thenReturn(ResponseEntity.ok(mockResponseDTO));

        // Act & Assert
        assertDoesNotThrow(() -> stockClientAdapter.buyStock(mockStock));
        
        verify(stockClientMapper, times(1)).toDtoRequest(mockStock);
        verify(stockClient, times(1)).buyStock(mockBuyRequest);
    }

    @Test
    @DisplayName("Should handle 201 Created response for buy stock")
    void testBuyStock_CreatedResponse_ReturnsNormally() {
        // Arrange
        when(stockClientMapper.toDtoRequest(mockStock)).thenReturn(mockBuyRequest);
        when(stockClient.buyStock(mockBuyRequest))
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(mockResponseDTO));

        // Act & Assert
        assertDoesNotThrow(() -> stockClientAdapter.buyStock(mockStock));
        
        verify(stockClient, times(1)).buyStock(mockBuyRequest);
    }

    @Test
    @DisplayName("Should throw exception when buy stock fails with 4xx error")
    void testBuyStock_ClientError_ThrowsException() {
        // Arrange
        when(stockClientMapper.toDtoRequest(mockStock)).thenReturn(mockBuyRequest);
        when(stockClient.buyStock(mockBuyRequest))
            .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockResponseDTO));
        doThrow(new GenericErrorException(400, "Failed to buy stock"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());

        // Act & Assert
        GenericErrorException exception = assertThrows(GenericErrorException.class, 
            () -> stockClientAdapter.buyStock(mockStock)
        );
        
        assertEquals("Failed to buy stock", exception.getMessage());
        verify(stockClient, times(1)).buyStock(mockBuyRequest);
        verify(formatterResultOutputPort).returnErrorGenericResponse(400, "Failed to buy stock");
    }

    @Test
    @DisplayName("Should throw exception when buy stock fails with 5xx error")
    void testBuyStock_ServerError_ThrowsException() {
        // Arrange
        when(stockClientMapper.toDtoRequest(mockStock)).thenReturn(mockBuyRequest);
        when(stockClient.buyStock(mockBuyRequest))
            .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mockResponseDTO));
        doThrow(new GenericErrorException(500, "Failed to buy stock"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());

        // Act & Assert
        GenericErrorException exception = assertThrows(GenericErrorException.class, 
            () -> stockClientAdapter.buyStock(mockStock)
        );
        
        assertEquals("Failed to buy stock", exception.getMessage());
        verify(stockClient, times(1)).buyStock(mockBuyRequest);
    }

    @Test
    @DisplayName("Should throw exception when buy stock returns 404")
    void testBuyStock_NotFoundError_ThrowsException() {
        // Arrange
        when(stockClientMapper.toDtoRequest(mockStock)).thenReturn(mockBuyRequest);
        when(stockClient.buyStock(mockBuyRequest))
            .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockResponseDTO));
        doThrow(new GenericErrorException(404, "Failed to buy stock"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());

        // Act & Assert
        GenericErrorException exception = assertThrows(GenericErrorException.class, 
            () -> stockClientAdapter.buyStock(mockStock)
        );
        
        assertEquals("Failed to buy stock", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle large quantity buy stock")
    void testBuyStock_LargeQuantity_Success() {
        // Arrange
        mockStock.setQuantity(10000);
        when(stockClientMapper.toDtoRequest(mockStock)).thenReturn(mockBuyRequest);
        when(stockClient.buyStock(mockBuyRequest))
            .thenReturn(ResponseEntity.ok(mockResponseDTO));

        // Act & Assert
        assertDoesNotThrow(() -> stockClientAdapter.buyStock(mockStock));
    }

    @Test
    @DisplayName("Should handle high price buy stock")
    void testBuyStock_HighPrice_Success() {
        // Arrange
        mockStock.setPrice(BigDecimal.valueOf(999999.99));
        when(stockClientMapper.toDtoRequest(mockStock)).thenReturn(mockBuyRequest);
        when(stockClient.buyStock(mockBuyRequest))
            .thenReturn(ResponseEntity.ok(mockResponseDTO));

        // Act & Assert
        assertDoesNotThrow(() -> stockClientAdapter.buyStock(mockStock));
    }

    // ==================== sellStock() ====================
    @Test
    @DisplayName("Should sell stock successfully")
    void testSellStock_Success_ReturnsNormally() {
        // Arrange
        when(stockClientMapper.toSellDtoRequest(mockStock)).thenReturn(mockSellRequest);
        when(stockClient.sellStock(mockSellRequest))
            .thenReturn(ResponseEntity.ok(mockResponseDTO));

        // Act & Assert
        assertDoesNotThrow(() -> stockClientAdapter.sellStock(mockStock));
        
        verify(stockClientMapper, times(1)).toSellDtoRequest(mockStock);
        verify(stockClient, times(1)).sellStock(mockSellRequest);
    }

    @Test
    @DisplayName("Should handle 201 Created response for sell stock")
    void testSellStock_CreatedResponse_ReturnsNormally() {
        // Arrange
        when(stockClientMapper.toSellDtoRequest(mockStock)).thenReturn(mockSellRequest);
        when(stockClient.sellStock(mockSellRequest))
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(mockResponseDTO));

        // Act & Assert
        assertDoesNotThrow(() -> stockClientAdapter.sellStock(mockStock));
        
        verify(stockClient, times(1)).sellStock(mockSellRequest);
    }

    @Test
    @DisplayName("Should throw exception when sell stock fails with 4xx error")
    void testSellStock_ClientError_ThrowsException() {
        // Arrange
        when(stockClientMapper.toSellDtoRequest(mockStock)).thenReturn(mockSellRequest);
        when(stockClient.sellStock(mockSellRequest))
            .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mockResponseDTO));
        doThrow(new GenericErrorException(400, "Failed to sell stock"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());

        // Act & Assert
        GenericErrorException exception = assertThrows(GenericErrorException.class, 
            () -> stockClientAdapter.sellStock(mockStock)
        );
        
        assertEquals("Failed to sell stock", exception.getMessage());
        verify(stockClient, times(1)).sellStock(mockSellRequest);
    }

    @Test
    @DisplayName("Should throw exception when sell stock fails with 5xx error")
    void testSellStock_ServerError_ThrowsException() {
        // Arrange
        when(stockClientMapper.toSellDtoRequest(mockStock)).thenReturn(mockSellRequest);
        when(stockClient.sellStock(mockSellRequest))
            .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mockResponseDTO));
        doThrow(new GenericErrorException(500, "Failed to sell stock"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());

        // Act & Assert
        GenericErrorException exception = assertThrows(GenericErrorException.class, 
            () -> stockClientAdapter.sellStock(mockStock)
        );
        
        assertEquals("Failed to sell stock", exception.getMessage());
        verify(stockClient, times(1)).sellStock(mockSellRequest);
    }

    @Test
    @DisplayName("Should throw exception when sell stock returns 404")
    void testSellStock_NotFoundError_ThrowsException() {
        // Arrange
        when(stockClientMapper.toSellDtoRequest(mockStock)).thenReturn(mockSellRequest);
        when(stockClient.sellStock(mockSellRequest))
            .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(mockResponseDTO));
        doThrow(new GenericErrorException(404, "Failed to sell stock"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());

        // Act & Assert
        GenericErrorException exception = assertThrows(GenericErrorException.class, 
            () -> stockClientAdapter.sellStock(mockStock)
        );
        
        assertEquals("Failed to sell stock", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when sell stock returns 409 Conflict")
    void testSellStock_ConflictError_ThrowsException() {
        // Arrange
        when(stockClientMapper.toSellDtoRequest(mockStock)).thenReturn(mockSellRequest);
        when(stockClient.sellStock(mockSellRequest))
            .thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).body(mockResponseDTO));
        doThrow(new GenericErrorException(409, "Failed to sell stock"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());

        // Act & Assert
        GenericErrorException exception = assertThrows(GenericErrorException.class, 
            () -> stockClientAdapter.sellStock(mockStock)
        );
        
        assertEquals("Failed to sell stock", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle large quantity sell stock")
    void testSellStock_LargeQuantity_Success() {
        // Arrange
        mockStock.setQuantity(5000);
        when(stockClientMapper.toSellDtoRequest(mockStock)).thenReturn(mockSellRequest);
        when(stockClient.sellStock(mockSellRequest))
            .thenReturn(ResponseEntity.ok(mockResponseDTO));

        // Act & Assert
        assertDoesNotThrow(() -> stockClientAdapter.sellStock(mockStock));
    }

    // ==================== Integration Scenarios ====================
    @Test
    @DisplayName("Should handle buy and sell operations sequentially")
    void testBuyAndSellSequence_Success() {
        // Arrange
        when(stockClientMapper.toDtoRequest(mockStock)).thenReturn(mockBuyRequest);
        when(stockClientMapper.toSellDtoRequest(mockStock)).thenReturn(mockSellRequest);
        when(stockClient.buyStock(mockBuyRequest))
            .thenReturn(ResponseEntity.ok(mockResponseDTO));
        when(stockClient.sellStock(mockSellRequest))
            .thenReturn(ResponseEntity.ok(mockResponseDTO));

        // Act & Assert
        assertDoesNotThrow(() -> {
            stockClientAdapter.buyStock(mockStock);
            stockClientAdapter.sellStock(mockStock);
        });
        
        verify(stockClient, times(1)).buyStock(mockBuyRequest);
        verify(stockClient, times(1)).sellStock(mockSellRequest);
    }

    @Test
    @DisplayName("Should use correct mapper method for buy operation")
    void testBuyStock_UsesCorrectMapperMethod() {
        // Arrange
        when(stockClientMapper.toDtoRequest(mockStock)).thenReturn(mockBuyRequest);
        when(stockClient.buyStock(any())).thenReturn(ResponseEntity.ok(mockResponseDTO));

        // Act
        stockClientAdapter.buyStock(mockStock);

        // Assert
        verify(stockClientMapper, times(1)).toDtoRequest(mockStock);
        verify(stockClientMapper, never()).toSellDtoRequest(any());
    }

    @Test
    @DisplayName("Should use correct mapper method for sell operation")
    void testSellStock_UsesCorrectMapperMethod() {
        // Arrange
        when(stockClientMapper.toSellDtoRequest(mockStock)).thenReturn(mockSellRequest);
        when(stockClient.sellStock(any())).thenReturn(ResponseEntity.ok(mockResponseDTO));

        // Act
        stockClientAdapter.sellStock(mockStock);

        // Assert
        verify(stockClientMapper, times(1)).toSellDtoRequest(mockStock);
        verify(stockClientMapper, never()).toDtoRequest(any());
    }

    @Test
    @DisplayName("Should handle multiple buy operations")
    void testMultipleBuyOperations_Success() {
        // Arrange
        when(stockClientMapper.toDtoRequest(any())).thenReturn(mockBuyRequest);
        when(stockClient.buyStock(any())).thenReturn(ResponseEntity.ok(mockResponseDTO));

        // Act & Assert
        assertDoesNotThrow(() -> {
            stockClientAdapter.buyStock(mockStock);
            stockClientAdapter.buyStock(mockStock);
            stockClientAdapter.buyStock(mockStock);
        });
        
        verify(stockClient, times(3)).buyStock(any());
    }

    @Test
    @DisplayName("Should handle multiple sell operations")
    void testMultipleSellOperations_Success() {
        // Arrange
        when(stockClientMapper.toSellDtoRequest(any())).thenReturn(mockSellRequest);
        when(stockClient.sellStock(any())).thenReturn(ResponseEntity.ok(mockResponseDTO));

        // Act & Assert
        assertDoesNotThrow(() -> {
            stockClientAdapter.sellStock(mockStock);
            stockClientAdapter.sellStock(mockStock);
            stockClientAdapter.sellStock(mockStock);
        });
        
        verify(stockClient, times(3)).sellStock(any());
    }

    // ==================== Edge Cases ====================
    @Test
    @DisplayName("Should handle null response body for buy stock")
    void testBuyStock_NullResponseBody_Success() {
        // Arrange
        when(stockClientMapper.toDtoRequest(mockStock)).thenReturn(mockBuyRequest);
        when(stockClient.buyStock(mockBuyRequest))
            .thenReturn(ResponseEntity.ok(null));

        // Act & Assert
        assertDoesNotThrow(() -> stockClientAdapter.buyStock(mockStock));
    }

    @Test
    @DisplayName("Should handle null response body for sell stock")
    void testSellStock_NullResponseBody_Success() {
        // Arrange
        when(stockClientMapper.toSellDtoRequest(mockStock)).thenReturn(mockSellRequest);
        when(stockClient.sellStock(mockSellRequest))
            .thenReturn(ResponseEntity.ok(null));

        // Act & Assert
        assertDoesNotThrow(() -> stockClientAdapter.sellStock(mockStock));
    }

}
