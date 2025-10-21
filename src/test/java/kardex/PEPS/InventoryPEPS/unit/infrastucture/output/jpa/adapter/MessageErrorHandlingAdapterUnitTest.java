package kardex.PEPS.InventoryPEPS.unit.infrastucture.output.jpa.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.adapter.MessageErrorHandlingAdapter;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.MessageProcessingErrorEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IMessageProcessingErrorRepository;

@ExtendWith(MockitoExtension.class)
public class MessageErrorHandlingAdapterUnitTest {
    

    @Mock
    private IMessageProcessingErrorRepository errorRepository;

    @InjectMocks
    private MessageErrorHandlingAdapter messageErrorHandlingAdapter;

    private String eventType;
    private String errorDescription;
    private String messageData;
    private String entityType;

    @BeforeEach
    void setUp() {
        // Arrange - Common setup
        eventType = "PRODUCT_CREATED";
        errorDescription = "Failed to process product creation";
        messageData = "{\"productId\": 123, \"name\": \"Test Product\"}";
        entityType = "Product";
    }

    // ==================== saveProcessingError() ====================
    @Test
    @DisplayName("Should save processing error with all fields")
    void testSaveProcessingError_ValidData_SavesSuccessfully() {
        // Arrange
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(eventType, errorDescription, messageData, entityType);

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertEquals(eventType, savedEntity.getEventType());
        assertEquals(errorDescription, savedEntity.getErrorDescription());
        assertEquals(messageData, savedEntity.getMessageData());
        assertEquals(entityType, savedEntity.getEntityType());
    }

    @Test
    @DisplayName("Should handle null event type by setting default value")
    void testSaveProcessingError_NullEventType_SetsDefaultValue() {
        // Arrange
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(null, errorDescription, messageData, entityType);

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertEquals("null_event_type", savedEntity.getEventType());
    }

    @Test
    @DisplayName("Should save error with empty event type")
    void testSaveProcessingError_EmptyEventType_SavesEmptyString() {
        // Arrange
        String emptyEventType = "";
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(emptyEventType, errorDescription, messageData, entityType);

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertEquals(emptyEventType, savedEntity.getEventType());
    }

    @Test
    @DisplayName("Should save error with null error description")
    void testSaveProcessingError_NullErrorDescription_SavesNull() {
        // Arrange
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(eventType, null, messageData, entityType);

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertNull(savedEntity.getErrorDescription());
    }

    @Test
    @DisplayName("Should save error with null message data")
    void testSaveProcessingError_NullMessageData_SavesNull() {
        // Arrange
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(eventType, errorDescription, null, entityType);

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertNull(savedEntity.getMessageData());
    }

    @Test
    @DisplayName("Should save error with null entity type")
    void testSaveProcessingError_NullEntityType_SavesNull() {
        // Arrange
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(eventType, errorDescription, messageData, null);

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertNull(savedEntity.getEntityType());
    }

    @Test
    @DisplayName("Should save error with long message data")
    void testSaveProcessingError_LongMessageData_SavesSuccessfully() {
        // Arrange
        String longMessage = "A".repeat(5000);
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(eventType, errorDescription, longMessage, entityType);

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertEquals(longMessage, savedEntity.getMessageData());
    }

    @Test
    @DisplayName("Should save error with special characters in description")
    void testSaveProcessingError_SpecialCharacters_SavesSuccessfully() {
        // Arrange
        String specialCharsDescription = "Error: <>&\"'ñáéíóú©®™";
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(eventType, specialCharsDescription, messageData, entityType);

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertEquals(specialCharsDescription, savedEntity.getErrorDescription());
    }

    // ==================== Error Handling ====================
    @Test
    @DisplayName("Should handle repository exception gracefully")
    void testSaveProcessingError_RepositoryThrowsException_DoesNotPropagate() {
        // Arrange
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        assertDoesNotThrow(() -> 
            messageErrorHandlingAdapter.saveProcessingError(eventType, errorDescription, messageData, entityType)
        );
        
        verify(errorRepository).save(any(MessageProcessingErrorEntity.class));
    }

    @Test
    @DisplayName("Should continue execution when save fails")
    void testSaveProcessingError_SaveFails_ContinuesExecution() {
        // Arrange
        doThrow(new RuntimeException("Constraint violation"))
            .when(errorRepository).save(any(MessageProcessingErrorEntity.class));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(eventType, errorDescription, messageData, entityType);

        // Assert
        verify(errorRepository).save(any(MessageProcessingErrorEntity.class));
        // No exception should be thrown, method completes normally
    }

    // ==================== Multiple Saves ====================
    @Test
    @DisplayName("Should handle multiple consecutive saves")
    void testSaveProcessingError_MultipleSaves_AllSucceed() {
        // Arrange
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError("EVENT_1", "Error 1", "Data 1", "Entity 1");
        messageErrorHandlingAdapter.saveProcessingError("EVENT_2", "Error 2", "Data 2", "Entity 2");
        messageErrorHandlingAdapter.saveProcessingError("EVENT_3", "Error 3", "Data 3", "Entity 3");

        // Assert
        verify(errorRepository, times(3)).save(any(MessageProcessingErrorEntity.class));
    }

    @Test
    @DisplayName("Should save different entity types")
    void testSaveProcessingError_DifferentEntityTypes_SavesCorrectly() {
        // Arrange
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(eventType, errorDescription, messageData, "Product");
        messageErrorHandlingAdapter.saveProcessingError(eventType, errorDescription, messageData, "Kardex");
        messageErrorHandlingAdapter.saveProcessingError(eventType, errorDescription, messageData, "Sale");

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository, times(3)).save(captor.capture());
        
        assertEquals("Product", captor.getAllValues().get(0).getEntityType());
        assertEquals("Kardex", captor.getAllValues().get(1).getEntityType());
        assertEquals("Sale", captor.getAllValues().get(2).getEntityType());
    }

    // ==================== Edge Cases ====================
    @Test
    @DisplayName("Should handle all null parameters")
    void testSaveProcessingError_AllNullParameters_SavesWithDefaults() {
        // Arrange
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(null, null, null, null);

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertEquals("null_event_type", savedEntity.getEventType());
        assertNull(savedEntity.getErrorDescription());
        assertNull(savedEntity.getMessageData());
        assertNull(savedEntity.getEntityType());
    }

    @Test
    @DisplayName("Should handle empty string parameters")
    void testSaveProcessingError_EmptyStrings_SavesEmptyValues() {
        // Arrange
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError("", "", "", "");

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertEquals("", savedEntity.getEventType());
        assertEquals("", savedEntity.getErrorDescription());
        assertEquals("", savedEntity.getMessageData());
        assertEquals("", savedEntity.getEntityType());
    }

    @Test
    @DisplayName("Should save error with JSON message data")
    void testSaveProcessingError_JSONMessageData_SavesCorrectly() {
        // Arrange
        String jsonMessage = "{\"id\":123,\"status\":\"error\",\"nested\":{\"field\":\"value\"}}";
        when(errorRepository.save(any(MessageProcessingErrorEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        messageErrorHandlingAdapter.saveProcessingError(eventType, errorDescription, jsonMessage, entityType);

        // Assert
        ArgumentCaptor<MessageProcessingErrorEntity> captor = ArgumentCaptor.forClass(MessageProcessingErrorEntity.class);
        verify(errorRepository).save(captor.capture());
        
        MessageProcessingErrorEntity savedEntity = captor.getValue();
        assertEquals(jsonMessage, savedEntity.getMessageData());
    }
}
