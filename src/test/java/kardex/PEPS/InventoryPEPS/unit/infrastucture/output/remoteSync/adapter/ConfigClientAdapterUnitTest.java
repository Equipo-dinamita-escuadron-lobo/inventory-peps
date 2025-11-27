package kardex.PEPS.InventoryPEPS.unit.infrastucture.output.remoteSync.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.exception.customized.GenericErrorException;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.adapter.ConfigClientAdapter;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IConfigClient;

/**
 * @brief Unit tests for ConfigClientAdapter
 * 
 * Tests the adapter for configuration service communication.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigClientAdapter Tests")
public class ConfigClientAdapterUnitTest {
    
    @Mock
    private IConfigClient configClient;
    
    @Mock
    private IFormatterResultOutputPort formatterResultOutputPort;
    
    @InjectMocks
    private ConfigClientAdapter configClientAdapter;
    
    private String enterpriseId;
    private LocalDate validDate;
    private LocalDate invalidDate;
    
    @BeforeEach
    void setUp() {
        enterpriseId = "ENT-001";
        validDate = LocalDate.of(2024, 11, 15);
        invalidDate = LocalDate.of(2023, 1, 1);
    }
    
    @Test
    @DisplayName("Should return true when date is valid")
    void testIsValidAccountingDate_ValidDate() {
        // Arrange
        when(configClient.existsDate(enterpriseId, validDate)).thenReturn(true);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(enterpriseId, validDate);
        
        // Assert
        assertTrue(result);
        verify(configClient, times(1)).existsDate(enterpriseId, validDate);
        verify(formatterResultOutputPort, never()).returnErrorGenericResponse(anyInt(), anyString());
    }
    
    @Test
    @DisplayName("Should return false when date is invalid")
    void testIsValidAccountingDate_InvalidDate() {
        // Arrange
        when(configClient.existsDate(enterpriseId, invalidDate)).thenReturn(false);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(enterpriseId, invalidDate);
        
        // Assert
        assertFalse(result);
        verify(configClient, times(1)).existsDate(enterpriseId, invalidDate);
        verify(formatterResultOutputPort, never()).returnErrorGenericResponse(anyInt(), anyString());
    }
    
    @Test
    @DisplayName("Should return false and handle ServiceUnavailable exception (503)")
    void testIsValidAccountingDate_ServiceUnavailable() {
        // Arrange
        WebClientResponseException exception = 
            WebClientResponseException.create(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service Unavailable", null, null, null);
        when(configClient.existsDate(enterpriseId, validDate)).thenThrow(exception);
        doThrow(new GenericErrorException(503, "Configuration service is unavailable"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());
        
        // Act & Assert
        GenericErrorException thrownException = assertThrows(GenericErrorException.class, 
            () -> configClientAdapter.isValidAccountingDate(enterpriseId, validDate)
        );
        
        assertEquals("Configuration service is unavailable", thrownException.getMessage());
        verify(formatterResultOutputPort).returnErrorGenericResponse(503, "Configuration service is unavailable");
    }
    
    @Test
    @DisplayName("Should return false and handle generic WebClientResponseException")
    void testIsValidAccountingDate_WebClientException() {
        // Arrange
        WebClientResponseException exception = 
            WebClientResponseException.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error", null, null, null);
        when(configClient.existsDate(enterpriseId, validDate)).thenThrow(exception);
        doThrow(new GenericErrorException(500, "Error communicating with configuration service"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());
        
        // Act & Assert
        GenericErrorException thrownException = assertThrows(GenericErrorException.class, 
            () -> configClientAdapter.isValidAccountingDate(enterpriseId, validDate)
        );
        
        assertEquals("Error communicating with configuration service", thrownException.getMessage());
        verify(formatterResultOutputPort).returnErrorGenericResponse(500, "Error communicating with configuration service");
    }
    
    @Test
    @DisplayName("Should return false and handle BadRequest exception (400)")
    void testIsValidAccountingDate_BadRequest() {
        // Arrange
        WebClientResponseException exception = 
            WebClientResponseException.create(HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null);
        when(configClient.existsDate(enterpriseId, validDate)).thenThrow(exception);
        doThrow(new GenericErrorException(500, "Error communicating with configuration service"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());
        
        // Act & Assert
        GenericErrorException thrownException = assertThrows(GenericErrorException.class, 
            () -> configClientAdapter.isValidAccountingDate(enterpriseId, validDate)
        );
        
        assertEquals("Error communicating with configuration service", thrownException.getMessage());
        verify(formatterResultOutputPort).returnErrorGenericResponse(500, "Error communicating with configuration service");
    }
    
    @Test
    @DisplayName("Should return false and handle unexpected exceptions")
    void testIsValidAccountingDate_UnexpectedException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Unexpected error");
        when(configClient.existsDate(enterpriseId, validDate)).thenThrow(exception);
        doThrow(new GenericErrorException(500, "Unexpected error communicating with configuration service"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());
        
        // Act & Assert
        GenericErrorException thrownException = assertThrows(GenericErrorException.class, 
            () -> configClientAdapter.isValidAccountingDate(enterpriseId, validDate)
        );
        
        assertEquals("Unexpected error communicating with configuration service", thrownException.getMessage());
        verify(formatterResultOutputPort).returnErrorGenericResponse(500, "Unexpected error communicating with configuration service");
    }
    
    @Test
    @DisplayName("Should handle NotFound exception (404)")
    void testIsValidAccountingDate_NotFound() {
        // Arrange
        WebClientResponseException exception = 
            WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null);
        when(configClient.existsDate(enterpriseId, validDate)).thenThrow(exception);
        doThrow(new GenericErrorException(500, "Error communicating with configuration service"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());
        
        // Act & Assert
        GenericErrorException thrownException = assertThrows(GenericErrorException.class, 
            () -> configClientAdapter.isValidAccountingDate(enterpriseId, validDate)
        );
        
        assertEquals("Error communicating with configuration service", thrownException.getMessage());
        verify(formatterResultOutputPort).returnErrorGenericResponse(500, "Error communicating with configuration service");
    }
    
    @Test
    @DisplayName("Should handle different enterprise IDs")
    void testIsValidAccountingDate_DifferentEnterpriseIds() {
        // Arrange
        String enterpriseId1 = "ENT-001";
        String enterpriseId2 = "ENT-999";
        
        when(configClient.existsDate(enterpriseId1, validDate)).thenReturn(true);
        when(configClient.existsDate(enterpriseId2, validDate)).thenReturn(false);
        
        // Act
        boolean result1 = configClientAdapter.isValidAccountingDate(enterpriseId1, validDate);
        boolean result2 = configClientAdapter.isValidAccountingDate(enterpriseId2, validDate);
        
        // Assert
        assertTrue(result1);
        assertFalse(result2);
        verify(configClient, times(1)).existsDate(enterpriseId1, validDate);
        verify(configClient, times(1)).existsDate(enterpriseId2, validDate);
    }
    
    @Test
    @DisplayName("Should handle different dates")
    void testIsValidAccountingDate_DifferentDates() {
        // Arrange
        LocalDate date1 = LocalDate.of(2024, 11, 15);
        LocalDate date2 = LocalDate.of(2024, 12, 20);
        
        when(configClient.existsDate(enterpriseId, date1)).thenReturn(true);
        when(configClient.existsDate(enterpriseId, date2)).thenReturn(false);
        
        // Act
        boolean result1 = configClientAdapter.isValidAccountingDate(enterpriseId, date1);
        boolean result2 = configClientAdapter.isValidAccountingDate(enterpriseId, date2);
        
        // Assert
        assertTrue(result1);
        assertFalse(result2);
        verify(configClient, times(1)).existsDate(enterpriseId, date1);
        verify(configClient, times(1)).existsDate(enterpriseId, date2);
    }
    
    @Test
    @DisplayName("Should handle current date")
    void testIsValidAccountingDate_CurrentDate() {
        // Arrange
        LocalDate currentDate = LocalDate.now();
        when(configClient.existsDate(enterpriseId, currentDate)).thenReturn(true);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(enterpriseId, currentDate);
        
        // Assert
        assertTrue(result);
        verify(configClient, times(1)).existsDate(enterpriseId, currentDate);
    }
    
    @Test
    @DisplayName("Should handle past date")
    void testIsValidAccountingDate_PastDate() {
        // Arrange
        LocalDate pastDate = LocalDate.of(2020, 1, 1);
        when(configClient.existsDate(enterpriseId, pastDate)).thenReturn(false);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(enterpriseId, pastDate);
        
        // Assert
        assertFalse(result);
        verify(configClient, times(1)).existsDate(enterpriseId, pastDate);
    }
    
    @Test
    @DisplayName("Should handle future date")
    void testIsValidAccountingDate_FutureDate() {
        // Arrange
        LocalDate futureDate = LocalDate.of(2030, 12, 31);
        when(configClient.existsDate(enterpriseId, futureDate)).thenReturn(true);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(enterpriseId, futureDate);
        
        // Assert
        assertTrue(result);
        verify(configClient, times(1)).existsDate(enterpriseId, futureDate);
    }
    
    @Test
    @DisplayName("Should handle year end date")
    void testIsValidAccountingDate_YearEndDate() {
        // Arrange
        LocalDate yearEndDate = LocalDate.of(2024, 12, 31);
        when(configClient.existsDate(enterpriseId, yearEndDate)).thenReturn(true);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(enterpriseId, yearEndDate);
        
        // Assert
        assertTrue(result);
        verify(configClient, times(1)).existsDate(enterpriseId, yearEndDate);
    }
    
    @Test
    @DisplayName("Should handle year start date")
    void testIsValidAccountingDate_YearStartDate() {
        // Arrange
        LocalDate yearStartDate = LocalDate.of(2024, 1, 1);
        when(configClient.existsDate(enterpriseId, yearStartDate)).thenReturn(true);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(enterpriseId, yearStartDate);
        
        // Assert
        assertTrue(result);
        verify(configClient, times(1)).existsDate(enterpriseId, yearStartDate);
    }
    
    @Test
    @DisplayName("Should handle leap year date")
    void testIsValidAccountingDate_LeapYearDate() {
        // Arrange
        LocalDate leapYearDate = LocalDate.of(2024, 2, 29);
        when(configClient.existsDate(enterpriseId, leapYearDate)).thenReturn(true);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(enterpriseId, leapYearDate);
        
        // Assert
        assertTrue(result);
        verify(configClient, times(1)).existsDate(enterpriseId, leapYearDate);
    }
    
    @Test
    @DisplayName("Should verify exact parameters passed to config client")
    void testIsValidAccountingDate_ParameterVerification() {
        // Arrange
        when(configClient.existsDate(enterpriseId, validDate)).thenReturn(true);
        
        // Act
        configClientAdapter.isValidAccountingDate(enterpriseId, validDate);
        
        // Assert
        verify(configClient, times(1)).existsDate(eq(enterpriseId), eq(validDate));
    }
    
    @Test
    @DisplayName("Should handle multiple consecutive calls")
    void testIsValidAccountingDate_MultipleConsecutiveCalls() {
        // Arrange
        when(configClient.existsDate(enterpriseId, validDate)).thenReturn(true);
        
        // Act
        boolean result1 = configClientAdapter.isValidAccountingDate(enterpriseId, validDate);
        boolean result2 = configClientAdapter.isValidAccountingDate(enterpriseId, validDate);
        boolean result3 = configClientAdapter.isValidAccountingDate(enterpriseId, validDate);
        
        // Assert
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        verify(configClient, times(3)).existsDate(enterpriseId, validDate);
    }
    
    @Test
    @DisplayName("Should handle Unauthorized exception (401)")
    void testIsValidAccountingDate_Unauthorized() {
        // Arrange
        WebClientResponseException exception = 
            WebClientResponseException.create(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", null, null, null);
        when(configClient.existsDate(enterpriseId, validDate)).thenThrow(exception);
        doThrow(new GenericErrorException(500, "Error communicating with configuration service"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());
        
        // Act & Assert
        GenericErrorException thrownException = assertThrows(GenericErrorException.class, 
            () -> configClientAdapter.isValidAccountingDate(enterpriseId, validDate)
        );
        
        assertEquals("Error communicating with configuration service", thrownException.getMessage());
        verify(formatterResultOutputPort).returnErrorGenericResponse(500, "Error communicating with configuration service");
    }
    
    @Test
    @DisplayName("Should handle Forbidden exception (403)")
    void testIsValidAccountingDate_Forbidden() {
        // Arrange
        WebClientResponseException exception = 
            WebClientResponseException.create(HttpStatus.FORBIDDEN.value(), "Forbidden", null, null, null);
        when(configClient.existsDate(enterpriseId, validDate)).thenThrow(exception);
        doThrow(new GenericErrorException(500, "Error communicating with configuration service"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());
        
        // Act & Assert
        GenericErrorException thrownException = assertThrows(GenericErrorException.class, 
            () -> configClientAdapter.isValidAccountingDate(enterpriseId, validDate)
        );
        
        assertEquals("Error communicating with configuration service", thrownException.getMessage());
        verify(formatterResultOutputPort).returnErrorGenericResponse(500, "Error communicating with configuration service");
    }
    
    @Test
    @DisplayName("Should handle Gateway Timeout exception (504)")
    void testIsValidAccountingDate_GatewayTimeout() {
        // Arrange
        WebClientResponseException exception = 
            WebClientResponseException.create(HttpStatus.GATEWAY_TIMEOUT.value(), "Gateway Timeout", null, null, null);
        when(configClient.existsDate(enterpriseId, validDate)).thenThrow(exception);
        doThrow(new GenericErrorException(500, "Error communicating with configuration service"))
            .when(formatterResultOutputPort).returnErrorGenericResponse(anyInt(), anyString());
        
        // Act & Assert
        GenericErrorException thrownException = assertThrows(GenericErrorException.class, 
            () -> configClientAdapter.isValidAccountingDate(enterpriseId, validDate)
        );
        
        assertEquals("Error communicating with configuration service", thrownException.getMessage());
        verify(formatterResultOutputPort).returnErrorGenericResponse(500, "Error communicating with configuration service");
    }
    
    @Test
    @DisplayName("Should handle null enterprise ID")
    void testIsValidAccountingDate_NullEnterpriseId() {
        // Arrange
        when(configClient.existsDate(null, validDate)).thenReturn(false);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(null, validDate);
        
        // Assert
        assertFalse(result);
        verify(configClient, times(1)).existsDate(null, validDate);
    }
    
    @Test
    @DisplayName("Should handle null date")
    void testIsValidAccountingDate_NullDate() {
        // Arrange
        when(configClient.existsDate(enterpriseId, null)).thenReturn(false);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(enterpriseId, null);
        
        // Assert
        assertFalse(result);
        verify(configClient, times(1)).existsDate(enterpriseId, null);
    }
    
    @Test
    @DisplayName("Should handle empty enterprise ID")
    void testIsValidAccountingDate_EmptyEnterpriseId() {
        // Arrange
        String emptyId = "";
        when(configClient.existsDate(emptyId, validDate)).thenReturn(false);
        
        // Act
        boolean result = configClientAdapter.isValidAccountingDate(emptyId, validDate);
        
        // Assert
        assertFalse(result);
        verify(configClient, times(1)).existsDate(emptyId, validDate);
    }
}

