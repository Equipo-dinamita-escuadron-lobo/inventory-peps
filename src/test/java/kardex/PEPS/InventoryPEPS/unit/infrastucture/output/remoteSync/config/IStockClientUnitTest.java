package kardex.PEPS.InventoryPEPS.unit.infrastucture.output.remoteSync.config;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PutExchange;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.ResponseDTO;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IStockClient;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockBuyDtoRequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockDtoResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockSellDtoRequest;

/**
 * Unit tests for {@link IStockClient}
 * 
 * Tests the HTTP client interface structure and annotations for Stock Service integration.
 * Validates HTTP method declarations, parameter annotations, and return types.
 * 
 * @author Equipo-dinamita-escuadron-lobo
 */
@DisplayName("IStockClient Unit Tests")
public class IStockClientUnitTest {

    @Nested
    @DisplayName("Interface Structure Tests")
    class InterfaceStructureTests {

        @Test
        @DisplayName("Should be an interface")
        void shouldBeInterface() {
            // Arrange & Act & Assert
            assertTrue(IStockClient.class.isInterface(), 
                "IStockClient should be an interface");
        }

        @Test
        @DisplayName("Should be public")
        void shouldBePublic() {
            // Arrange & Act
            int modifiers = IStockClient.class.getModifiers();

            // Assert
            assertTrue(java.lang.reflect.Modifier.isPublic(modifiers), 
                "IStockClient should be public");
        }

        @Test
        @DisplayName("Should have exactly 2 methods")
        void shouldHaveExactlyTwoMethods() {
            // Arrange & Act
            Method[] methods = IStockClient.class.getDeclaredMethods();

            // Assert
            assertEquals(2, methods.length, 
                "IStockClient should have exactly 2 methods");
        }
    }

    @Nested
    @DisplayName("BuyStock Method Tests")
    class BuyStockMethodTests {

        @Test
        @DisplayName("Should have buyStock method")
        void shouldHaveBuyStockMethod() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> {
                IStockClient.class.getDeclaredMethod("buyStock", StockBuyDtoRequest.class);
            }, "IStockClient should have buyStock method");
        }

        @Test
        @DisplayName("BuyStock method should have PutExchange annotation")
        void buyStockMethodShouldHavePutExchangeAnnotation() throws Exception {
            // Arrange & Act
            Method buyStockMethod = IStockClient.class.getDeclaredMethod("buyStock", StockBuyDtoRequest.class);
            boolean hasPutExchange = buyStockMethod.isAnnotationPresent(PutExchange.class);

            // Assert
            assertTrue(hasPutExchange, 
                "buyStock method should have @PutExchange annotation");
        }

        @Test
        @DisplayName("BuyStock PutExchange should have correct path")
        void buyStockPutExchangeShouldHaveCorrectPath() throws Exception {
            // Arrange & Act
            Method buyStockMethod = IStockClient.class.getDeclaredMethod("buyStock", StockBuyDtoRequest.class);
            PutExchange putExchange = buyStockMethod.getAnnotation(PutExchange.class);

            // Assert
            assertNotNull(putExchange, "@PutExchange annotation should not be null");
            assertEquals("/api/stock/buy", putExchange.value(), 
                "PutExchange path should be '/api/stock/buy'");
        }

        @Test
        @DisplayName("BuyStock method should return ResponseEntity")
        void buyStockMethodShouldReturnResponseEntity() throws Exception {
            // Arrange & Act
            Method buyStockMethod = IStockClient.class.getDeclaredMethod("buyStock", StockBuyDtoRequest.class);
            Class<?> returnType = buyStockMethod.getReturnType();

            // Assert
            assertEquals(ResponseEntity.class, returnType, 
                "buyStock should return ResponseEntity");
        }

        @Test
        @DisplayName("BuyStock parameter should have RequestBody annotation")
        void buyStockParameterShouldHaveRequestBodyAnnotation() throws Exception {
            // Arrange & Act
            Method buyStockMethod = IStockClient.class.getDeclaredMethod("buyStock", StockBuyDtoRequest.class);
            var parameters = buyStockMethod.getParameters();

            // Assert
            assertEquals(1, parameters.length, "buyStock should have 1 parameter");
            assertTrue(parameters[0].isAnnotationPresent(RequestBody.class), 
                "Parameter should have @RequestBody annotation");
        }

        @Test
        @DisplayName("BuyStock parameter should be StockBuyDtoRequest type")
        void buyStockParameterShouldBeStockBuyDtoRequestType() throws Exception {
            // Arrange & Act
            Method buyStockMethod = IStockClient.class.getDeclaredMethod("buyStock", StockBuyDtoRequest.class);
            var parameters = buyStockMethod.getParameters();

            // Assert
            assertEquals(StockBuyDtoRequest.class, parameters[0].getType(), 
                "Parameter should be of type StockBuyDtoRequest");
        }
    }

    @Nested
    @DisplayName("SellStock Method Tests")
    class SellStockMethodTests {

        @Test
        @DisplayName("Should have sellStock method")
        void shouldHaveSellStockMethod() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> {
                IStockClient.class.getDeclaredMethod("sellStock", StockSellDtoRequest.class);
            }, "IStockClient should have sellStock method");
        }

        @Test
        @DisplayName("SellStock method should have PutExchange annotation")
        void sellStockMethodShouldHavePutExchangeAnnotation() throws Exception {
            // Arrange & Act
            Method sellStockMethod = IStockClient.class.getDeclaredMethod("sellStock", StockSellDtoRequest.class);
            boolean hasPutExchange = sellStockMethod.isAnnotationPresent(PutExchange.class);

            // Assert
            assertTrue(hasPutExchange, 
                "sellStock method should have @PutExchange annotation");
        }

        @Test
        @DisplayName("SellStock PutExchange should have correct path")
        void sellStockPutExchangeShouldHaveCorrectPath() throws Exception {
            // Arrange & Act
            Method sellStockMethod = IStockClient.class.getDeclaredMethod("sellStock", StockSellDtoRequest.class);
            PutExchange putExchange = sellStockMethod.getAnnotation(PutExchange.class);

            // Assert
            assertNotNull(putExchange, "@PutExchange annotation should not be null");
            assertEquals("/api/stock/sell", putExchange.value(), 
                "PutExchange path should be '/api/stock/sell'");
        }

        @Test
        @DisplayName("SellStock method should return ResponseEntity")
        void sellStockMethodShouldReturnResponseEntity() throws Exception {
            // Arrange & Act
            Method sellStockMethod = IStockClient.class.getDeclaredMethod("sellStock", StockSellDtoRequest.class);
            Class<?> returnType = sellStockMethod.getReturnType();

            // Assert
            assertEquals(ResponseEntity.class, returnType, 
                "sellStock should return ResponseEntity");
        }

        @Test
        @DisplayName("SellStock parameter should have RequestBody annotation")
        void sellStockParameterShouldHaveRequestBodyAnnotation() throws Exception {
            // Arrange & Act
            Method sellStockMethod = IStockClient.class.getDeclaredMethod("sellStock", StockSellDtoRequest.class);
            var parameters = sellStockMethod.getParameters();

            // Assert
            assertEquals(1, parameters.length, "sellStock should have 1 parameter");
            assertTrue(parameters[0].isAnnotationPresent(RequestBody.class), 
                "Parameter should have @RequestBody annotation");
        }

        @Test
        @DisplayName("SellStock parameter should be StockSellDtoRequest type")
        void sellStockParameterShouldBeStockSellDtoRequestType() throws Exception {
            // Arrange & Act
            Method sellStockMethod = IStockClient.class.getDeclaredMethod("sellStock", StockSellDtoRequest.class);
            var parameters = sellStockMethod.getParameters();

            // Assert
            assertEquals(StockSellDtoRequest.class, parameters[0].getType(), 
                "Parameter should be of type StockSellDtoRequest");
        }
    }

    @Nested
    @DisplayName("Method Naming Convention Tests")
    class MethodNamingConventionTests {

        @Test
        @DisplayName("BuyStock method name should be in camelCase")
        void buyStockMethodNameShouldBeInCamelCase() throws Exception {
            // Arrange & Act
            Method buyStockMethod = IStockClient.class.getDeclaredMethod("buyStock", StockBuyDtoRequest.class);
            String methodName = buyStockMethod.getName();

            // Assert
            assertEquals("buyStock", methodName, 
                "Method name should be 'buyStock' in camelCase");
        }

        @Test
        @DisplayName("SellStock method name should be in camelCase")
        void sellStockMethodNameShouldBeInCamelCase() throws Exception {
            // Arrange & Act
            Method sellStockMethod = IStockClient.class.getDeclaredMethod("sellStock", StockSellDtoRequest.class);
            String methodName = sellStockMethod.getName();

            // Assert
            assertEquals("sellStock", methodName, 
                "Method name should be 'sellStock' in camelCase");
        }
    }

    @Nested
    @DisplayName("Return Type Consistency Tests")
    class ReturnTypeConsistencyTests {

        @Test
        @DisplayName("Both methods should return same ResponseEntity structure")
        void bothMethodsShouldReturnSameResponseEntityStructure() throws Exception {
            // Arrange & Act
            Method buyStockMethod = IStockClient.class.getDeclaredMethod("buyStock", StockBuyDtoRequest.class);
            Method sellStockMethod = IStockClient.class.getDeclaredMethod("sellStock", StockSellDtoRequest.class);

            Class<?> buyReturnType = buyStockMethod.getReturnType();
            Class<?> sellReturnType = sellStockMethod.getReturnType();

            // Assert
            assertEquals(buyReturnType, sellReturnType, 
                "Both methods should return the same type");
            assertEquals(ResponseEntity.class, buyReturnType, 
                "Return type should be ResponseEntity");
        }

        @Test
        @DisplayName("BuyStock should return ResponseEntity of ResponseDTO with StockDtoResponse")
        void buyStockShouldReturnCorrectGenericType() throws Exception {
            // Arrange & Act
            Method buyStockMethod = IStockClient.class.getDeclaredMethod("buyStock", StockBuyDtoRequest.class);
            var genericReturnType = buyStockMethod.getGenericReturnType().getTypeName();

            // Assert
            assertTrue(genericReturnType.contains("ResponseEntity"), 
                "Generic return type should contain ResponseEntity");
            assertTrue(genericReturnType.contains("ResponseDTO"), 
                "Generic return type should contain ResponseDTO");
            assertTrue(genericReturnType.contains("StockDtoResponse"), 
                "Generic return type should contain StockDtoResponse");
        }

        @Test
        @DisplayName("SellStock should return ResponseEntity of ResponseDTO with StockDtoResponse")
        void sellStockShouldReturnCorrectGenericType() throws Exception {
            // Arrange & Act
            Method sellStockMethod = IStockClient.class.getDeclaredMethod("sellStock", StockSellDtoRequest.class);
            var genericReturnType = sellStockMethod.getGenericReturnType().getTypeName();

            // Assert
            assertTrue(genericReturnType.contains("ResponseEntity"), 
                "Generic return type should contain ResponseEntity");
            assertTrue(genericReturnType.contains("ResponseDTO"), 
                "Generic return type should contain ResponseDTO");
            assertTrue(genericReturnType.contains("StockDtoResponse"), 
                "Generic return type should contain StockDtoResponse");
        }
    }

    @Nested
    @DisplayName("HTTP Method Tests")
    class HttpMethodTests {

        @Test
        @DisplayName("BuyStock should use PUT HTTP method")
        void buyStockShouldUsePutHttpMethod() throws Exception {
            // Arrange & Act
            Method buyStockMethod = IStockClient.class.getDeclaredMethod("buyStock", StockBuyDtoRequest.class);
            boolean hasPutExchange = buyStockMethod.isAnnotationPresent(PutExchange.class);

            // Assert
            assertTrue(hasPutExchange, 
                "buyStock should use PUT HTTP method (@PutExchange)");
        }

        @Test
        @DisplayName("SellStock should use PUT HTTP method")
        void sellStockShouldUsePutHttpMethod() throws Exception {
            // Arrange & Act
            Method sellStockMethod = IStockClient.class.getDeclaredMethod("sellStock", StockSellDtoRequest.class);
            boolean hasPutExchange = sellStockMethod.isAnnotationPresent(PutExchange.class);

            // Assert
            assertTrue(hasPutExchange, 
                "sellStock should use PUT HTTP method (@PutExchange)");
        }
    }
}
