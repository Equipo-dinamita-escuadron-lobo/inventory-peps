package kardex.PEPS.InventoryPEPS.unit.infrastucture.output.remoteSync.config;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IProductClient;

/**
 * Unit tests for {@link IProductClient}
 * 
 * Tests the HTTP client interface structure and annotations for Product synchronization.
 * Validates HTTP method declarations, parameter annotations, and return types.
 * 
 * @author Equipo-dinamita-escuadron-lobo
 */
@DisplayName("IProductClient Unit Tests")
public class IProductClientUnitTest {

    @Nested
    @DisplayName("Interface Structure Tests")
    class InterfaceStructureTests {

        @Test
        @DisplayName("Should be an interface")
        void shouldBeInterface() {
            // Arrange & Act & Assert
            assertTrue(IProductClient.class.isInterface(), 
                "IProductClient should be an interface");
        }

        @Test
        @DisplayName("Should be public")
        void shouldBePublic() {
            // Arrange & Act
            int modifiers = IProductClient.class.getModifiers();

            // Assert
            assertTrue(java.lang.reflect.Modifier.isPublic(modifiers), 
                "IProductClient should be public");
        }

        @Test
        @DisplayName("Should have exactly 1 method")
        void shouldHaveExactlyOneMethod() {
            // Arrange & Act
            Method[] methods = IProductClient.class.getDeclaredMethods();

            // Assert
            assertEquals(1, methods.length, 
                "IProductClient should have exactly 1 method");
        }
    }

    @Nested
    @DisplayName("FindAllProductsByEnterpriseId Method Tests")
    class FindAllProductsByEnterpriseIdMethodTests {

        @Test
        @DisplayName("Should have findAllProductsByEnterpriseId method")
        void shouldHaveFindAllProductsByEnterpriseIdMethod() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> {
                IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            }, "IProductClient should have findAllProductsByEnterpriseId method");
        }

        @Test
        @DisplayName("Method should have GetExchange annotation")
        void methodShouldHaveGetExchangeAnnotation() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            boolean hasGetExchange = method.isAnnotationPresent(GetExchange.class);

            // Assert
            assertTrue(hasGetExchange, 
                "findAllProductsByEnterpriseId should have @GetExchange annotation");
        }

        @Test
        @DisplayName("GetExchange should have correct path")
        void getExchangeShouldHaveCorrectPath() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            GetExchange getExchange = method.getAnnotation(GetExchange.class);

            // Assert
            assertNotNull(getExchange, "@GetExchange annotation should not be null");
            assertEquals("/api/products/sync/findByEnterpriseId/{enterpriseId}", getExchange.value(), 
                "GetExchange path should be '/api/products/sync/findByEnterpriseId/{enterpriseId}'");
        }

        @Test
        @DisplayName("Method should return List")
        void methodShouldReturnList() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            Class<?> returnType = method.getReturnType();

            // Assert
            assertEquals(List.class, returnType, 
                "findAllProductsByEnterpriseId should return List");
        }

        @Test
        @DisplayName("Method should have 2 parameters")
        void methodShouldHaveTwoParameters() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            var parameters = method.getParameters();

            // Assert
            assertEquals(2, parameters.length, 
                "findAllProductsByEnterpriseId should have 2 parameters");
        }

        @Test
        @DisplayName("First parameter should have PathVariable annotation")
        void firstParameterShouldHavePathVariableAnnotation() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            var parameters = method.getParameters();

            // Assert
            assertTrue(parameters[0].isAnnotationPresent(PathVariable.class), 
                "First parameter should have @PathVariable annotation");
        }

        @Test
        @DisplayName("First parameter should be String type (enterpriseId)")
        void firstParameterShouldBeStringType() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            var parameters = method.getParameters();

            // Assert
            assertEquals(String.class, parameters[0].getType(), 
                "First parameter should be of type String");
        }

        @Test
        @DisplayName("Second parameter should have RequestParam annotation")
        void secondParameterShouldHaveRequestParamAnnotation() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            var parameters = method.getParameters();

            // Assert
            assertTrue(parameters[1].isAnnotationPresent(RequestParam.class), 
                "Second parameter should have @RequestParam annotation");
        }

        @Test
        @DisplayName("Second parameter should be Instant type (since)")
        void secondParameterShouldBeInstantType() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            var parameters = method.getParameters();

            // Assert
            assertEquals(Instant.class, parameters[1].getType(), 
                "Second parameter should be of type Instant");
        }
    }

    @Nested
    @DisplayName("Method Return Type Tests")
    class MethodReturnTypeTests {

        @Test
        @DisplayName("Should return List of ProductSyncDto")
        void shouldReturnListOfProductSyncDto() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            var genericReturnType = method.getGenericReturnType().getTypeName();

            // Assert
            assertTrue(genericReturnType.contains("List"), 
                "Generic return type should contain List");
            assertTrue(genericReturnType.contains("ProductSyncDto"), 
                "Generic return type should contain ProductSyncDto");
        }

        @Test
        @DisplayName("Return type should be a List collection")
        void returnTypeShouldBeListCollection() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            Class<?> returnType = method.getReturnType();

            // Assert
            assertTrue(List.class.isAssignableFrom(returnType), 
                "Return type should be assignable from List");
        }
    }

    @Nested
    @DisplayName("HTTP Method Tests")
    class HttpMethodTests {

        @Test
        @DisplayName("Should use GET HTTP method")
        void shouldUseGetHttpMethod() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            boolean hasGetExchange = method.isAnnotationPresent(GetExchange.class);

            // Assert
            assertTrue(hasGetExchange, 
                "findAllProductsByEnterpriseId should use GET HTTP method (@GetExchange)");
        }

        @Test
        @DisplayName("Should not have other HTTP method annotations")
        void shouldNotHaveOtherHttpMethodAnnotations() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            var annotations = method.getAnnotations();

            // Assert
            assertEquals(1, annotations.length, 
                "Method should have only one annotation (@GetExchange)");
        }
    }

    @Nested
    @DisplayName("Method Naming Convention Tests")
    class MethodNamingConventionTests {

        @Test
        @DisplayName("Method name should be in camelCase")
        void methodNameShouldBeInCamelCase() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            String methodName = method.getName();

            // Assert
            assertEquals("findAllProductsByEnterpriseId", methodName, 
                "Method name should be 'findAllProductsByEnterpriseId' in camelCase");
        }

        @Test
        @DisplayName("Method name should follow findAll naming pattern")
        void methodNameShouldFollowFindAllNamingPattern() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            String methodName = method.getName();

            // Assert
            assertTrue(methodName.startsWith("findAll"), 
                "Method name should start with 'findAll' following repository pattern");
        }
    }

    @Nested
    @DisplayName("Parameter Annotation Tests")
    class ParameterAnnotationTests {

        @Test
        @DisplayName("EnterpriseId parameter should be path variable")
        void enterpriseIdParameterShouldBePathVariable() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            var parameters = method.getParameters();
            PathVariable pathVariable = parameters[0].getAnnotation(PathVariable.class);

            // Assert
            assertNotNull(pathVariable, 
                "EnterpriseId parameter should have @PathVariable annotation");
        }

        @Test
        @DisplayName("Since parameter should be request param")
        void sinceParameterShouldBeRequestParam() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            var parameters = method.getParameters();
            RequestParam requestParam = parameters[1].getAnnotation(RequestParam.class);

            // Assert
            assertNotNull(requestParam, 
                "Since parameter should have @RequestParam annotation");
        }

        @Test
        @DisplayName("Parameters should have correct order")
        void parametersShouldHaveCorrectOrder() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            var parameters = method.getParameters();

            // Assert
            assertEquals(String.class, parameters[0].getType(), 
                "First parameter should be String (enterpriseId)");
            assertEquals(Instant.class, parameters[1].getType(), 
                "Second parameter should be Instant (since)");
        }
    }

    @Nested
    @DisplayName("URL Path Tests")
    class UrlPathTests {

        @Test
        @DisplayName("URL path should contain path variable placeholder")
        void urlPathShouldContainPathVariablePlaceholder() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            GetExchange getExchange = method.getAnnotation(GetExchange.class);
            String path = getExchange.value();

            // Assert
            assertTrue(path.contains("{enterpriseId}"), 
                "URL path should contain {enterpriseId} placeholder");
        }

        @Test
        @DisplayName("URL path should start with /api/products")
        void urlPathShouldStartWithApiProducts() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            GetExchange getExchange = method.getAnnotation(GetExchange.class);
            String path = getExchange.value();

            // Assert
            assertTrue(path.startsWith("/api/products"), 
                "URL path should start with '/api/products'");
        }

        @Test
        @DisplayName("URL path should contain sync endpoint")
        void urlPathShouldContainSyncEndpoint() throws Exception {
            // Arrange & Act
            Method method = IProductClient.class.getDeclaredMethod("findAllProductsByEnterpriseId", String.class, Instant.class);
            GetExchange getExchange = method.getAnnotation(GetExchange.class);
            String path = getExchange.value();

            // Assert
            assertTrue(path.contains("/sync/"), 
                "URL path should contain '/sync/' for synchronization endpoint");
        }
    }
}
