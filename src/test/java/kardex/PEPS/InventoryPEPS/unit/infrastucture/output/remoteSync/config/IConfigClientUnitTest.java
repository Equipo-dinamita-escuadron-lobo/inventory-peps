package kardex.PEPS.InventoryPEPS.unit.infrastucture.output.remoteSync.config;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IConfigClient;

/**
 * Unit tests for {@link IConfigClient}
 * 
 * Tests the HTTP client interface structure and annotations for Configuration Service integration.
 * Validates HTTP method declarations, parameter annotations, and return types.
 * 
 * @author Equipo-dinamita-escuadron-lobo
 */
@DisplayName("IConfigClient Unit Tests")
public class IConfigClientUnitTest {

    @Nested
    @DisplayName("Interface Structure Tests")
    class InterfaceStructureTests {

        @Test
        @DisplayName("Should be an interface")
        void shouldBeInterface() {
            // Arrange & Act & Assert
            assertTrue(IConfigClient.class.isInterface(), 
                "IConfigClient should be an interface");
        }

        @Test
        @DisplayName("Should be public")
        void shouldBePublic() {
            // Arrange & Act
            int modifiers = IConfigClient.class.getModifiers();

            // Assert
            assertTrue(java.lang.reflect.Modifier.isPublic(modifiers), 
                "IConfigClient should be public");
        }

        @Test
        @DisplayName("Should have exactly 1 method")
        void shouldHaveExactlyOneMethod() {
            // Arrange & Act
            Method[] methods = IConfigClient.class.getDeclaredMethods();

            // Assert
            assertEquals(1, methods.length, 
                "IConfigClient should have exactly 1 method");
        }
    }

    @Nested
    @DisplayName("ExistsDate Method Tests")
    class ExistsDateMethodTests {

        @Test
        @DisplayName("Should have existsDate method")
        void shouldHaveExistsDateMethod() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> {
                IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            }, "IConfigClient should have existsDate method");
        }

        @Test
        @DisplayName("Method should have GetExchange annotation")
        void methodShouldHaveGetExchangeAnnotation() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            boolean hasGetExchange = method.isAnnotationPresent(GetExchange.class);

            // Assert
            assertTrue(hasGetExchange, 
                "existsDate should have @GetExchange annotation");
        }

        @Test
        @DisplayName("GetExchange should have correct path")
        void getExchangeShouldHaveCorrectPath() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            GetExchange getExchange = method.getAnnotation(GetExchange.class);

            // Assert
            assertNotNull(getExchange, "@GetExchange annotation should not be null");
            assertEquals("/api/config/accounting-calendar/exists/{enterpriseId}", getExchange.value(), 
                "GetExchange path should be '/api/config/accounting-calendar/exists/{enterpriseId}'");
        }

        @Test
        @DisplayName("Method should return boolean")
        void methodShouldReturnBoolean() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            Class<?> returnType = method.getReturnType();

            // Assert
            assertEquals(boolean.class, returnType, 
                "existsDate should return boolean primitive");
        }

        @Test
        @DisplayName("Method should have 2 parameters")
        void methodShouldHaveTwoParameters() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();

            // Assert
            assertEquals(2, parameters.length, 
                "existsDate should have 2 parameters");
        }

        @Test
        @DisplayName("First parameter should have PathVariable annotation")
        void firstParameterShouldHavePathVariableAnnotation() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();

            // Assert
            assertTrue(parameters[0].isAnnotationPresent(PathVariable.class), 
                "First parameter should have @PathVariable annotation");
        }

        @Test
        @DisplayName("First parameter should be String type (enterpriseId)")
        void firstParameterShouldBeStringType() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();

            // Assert
            assertEquals(String.class, parameters[0].getType(), 
                "First parameter should be of type String");
        }

        @Test
        @DisplayName("Second parameter should have RequestParam annotation")
        void secondParameterShouldHaveRequestParamAnnotation() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();

            // Assert
            assertTrue(parameters[1].isAnnotationPresent(RequestParam.class), 
                "Second parameter should have @RequestParam annotation");
        }

        @Test
        @DisplayName("Second parameter should be LocalDate type")
        void secondParameterShouldBeLocalDateType() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();

            // Assert
            assertEquals(LocalDate.class, parameters[1].getType(), 
                "Second parameter should be of type LocalDate");
        }

        @Test
        @DisplayName("Second parameter should have DateTimeFormat annotation")
        void secondParameterShouldHaveDateTimeFormatAnnotation() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();

            // Assert
            assertTrue(parameters[1].isAnnotationPresent(DateTimeFormat.class), 
                "Second parameter should have @DateTimeFormat annotation");
        }

        @Test
        @DisplayName("DateTimeFormat should use ISO DATE format")
        void dateTimeFormatShouldUseIsoDateFormat() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();
            DateTimeFormat dateTimeFormat = parameters[1].getAnnotation(DateTimeFormat.class);

            // Assert
            assertNotNull(dateTimeFormat, "@DateTimeFormat annotation should not be null");
            assertEquals(DateTimeFormat.ISO.DATE, dateTimeFormat.iso(), 
                "DateTimeFormat should use ISO.DATE format");
        }
    }

    @Nested
    @DisplayName("Method Return Type Tests")
    class MethodReturnTypeTests {

        @Test
        @DisplayName("Should return primitive boolean not Boolean wrapper")
        void shouldReturnPrimitiveBooleanNotWrapper() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            Class<?> returnType = method.getReturnType();

            // Assert
            assertTrue(returnType.isPrimitive(), 
                "Return type should be primitive boolean");
            assertEquals(boolean.class, returnType, 
                "Return type should be boolean primitive, not Boolean wrapper");
        }

        @Test
        @DisplayName("Return type should indicate existence check")
        void returnTypeShouldIndicateExistenceCheck() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();

            // Assert
            assertTrue(methodName.startsWith("exists"), 
                "Method name should start with 'exists'");
            assertEquals(boolean.class, returnType, 
                "Existence check methods should return boolean");
        }
    }

    @Nested
    @DisplayName("HTTP Method Tests")
    class HttpMethodTests {

        @Test
        @DisplayName("Should use GET HTTP method")
        void shouldUseGetHttpMethod() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            boolean hasGetExchange = method.isAnnotationPresent(GetExchange.class);

            // Assert
            assertTrue(hasGetExchange, 
                "existsDate should use GET HTTP method (@GetExchange)");
        }

        @Test
        @DisplayName("GET method is appropriate for read-only existence check")
        void getMethodIsAppropriateForReadOnlyExistenceCheck() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            boolean hasGetExchange = method.isAnnotationPresent(GetExchange.class);

            // Assert
            assertTrue(hasGetExchange, 
                "Existence checks are read-only operations and should use GET");
        }
    }

    @Nested
    @DisplayName("Method Naming Convention Tests")
    class MethodNamingConventionTests {

        @Test
        @DisplayName("Method name should be in camelCase")
        void methodNameShouldBeInCamelCase() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            String methodName = method.getName();

            // Assert
            assertEquals("existsDate", methodName, 
                "Method name should be 'existsDate' in camelCase");
        }

        @Test
        @DisplayName("Method name should follow exists naming pattern")
        void methodNameShouldFollowExistsNamingPattern() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            String methodName = method.getName();

            // Assert
            assertTrue(methodName.startsWith("exists"), 
                "Method name should start with 'exists' for existence check operations");
        }
    }

    @Nested
    @DisplayName("Parameter Annotation Tests")
    class ParameterAnnotationTests {

        @Test
        @DisplayName("EnterpriseId parameter should be path variable")
        void enterpriseIdParameterShouldBePathVariable() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();
            PathVariable pathVariable = parameters[0].getAnnotation(PathVariable.class);

            // Assert
            assertNotNull(pathVariable, 
                "EnterpriseId parameter should have @PathVariable annotation");
        }

        @Test
        @DisplayName("Date parameter should be request param")
        void dateParameterShouldBeRequestParam() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();
            RequestParam requestParam = parameters[1].getAnnotation(RequestParam.class);

            // Assert
            assertNotNull(requestParam, 
                "Date parameter should have @RequestParam annotation");
        }

        @Test
        @DisplayName("Parameters should have correct order")
        void parametersShouldHaveCorrectOrder() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();

            // Assert
            assertEquals(String.class, parameters[0].getType(), 
                "First parameter should be String (enterpriseId)");
            assertEquals(LocalDate.class, parameters[1].getType(), 
                "Second parameter should be LocalDate (date)");
        }

        @Test
        @DisplayName("Date parameter should have both RequestParam and DateTimeFormat")
        void dateParameterShouldHaveBothAnnotations() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();

            // Assert
            assertTrue(parameters[1].isAnnotationPresent(RequestParam.class), 
                "Date parameter should have @RequestParam");
            assertTrue(parameters[1].isAnnotationPresent(DateTimeFormat.class), 
                "Date parameter should have @DateTimeFormat");
        }
    }

    @Nested
    @DisplayName("URL Path Tests")
    class UrlPathTests {

        @Test
        @DisplayName("URL path should contain path variable placeholder")
        void urlPathShouldContainPathVariablePlaceholder() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            GetExchange getExchange = method.getAnnotation(GetExchange.class);
            String path = getExchange.value();

            // Assert
            assertTrue(path.contains("{enterpriseId}"), 
                "URL path should contain {enterpriseId} placeholder");
        }

        @Test
        @DisplayName("URL path should start with /api/config")
        void urlPathShouldStartWithApiConfig() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            GetExchange getExchange = method.getAnnotation(GetExchange.class);
            String path = getExchange.value();

            // Assert
            assertTrue(path.startsWith("/api/config"), 
                "URL path should start with '/api/config'");
        }

        @Test
        @DisplayName("URL path should contain accounting-calendar resource")
        void urlPathShouldContainAccountingCalendarResource() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            GetExchange getExchange = method.getAnnotation(GetExchange.class);
            String path = getExchange.value();

            // Assert
            assertTrue(path.contains("accounting-calendar"), 
                "URL path should contain 'accounting-calendar' resource");
        }

        @Test
        @DisplayName("URL path should contain exists action")
        void urlPathShouldContainExistsAction() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            GetExchange getExchange = method.getAnnotation(GetExchange.class);
            String path = getExchange.value();

            // Assert
            assertTrue(path.contains("/exists/"), 
                "URL path should contain '/exists/' action");
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Method purpose should be accounting date validation")
        void methodPurposeShouldBeAccountingDateValidation() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            GetExchange getExchange = method.getAnnotation(GetExchange.class);
            String path = getExchange.value();

            // Assert
            assertTrue(path.contains("accounting-calendar"), 
                "Method should validate accounting calendar dates");
            assertEquals(boolean.class, method.getReturnType(), 
                "Should return boolean for validation result");
        }

        @Test
        @DisplayName("Method should validate dates for specific enterprise")
        void methodShouldValidateDatesForSpecificEnterprise() throws Exception {
            // Arrange & Act
            Method method = IConfigClient.class.getDeclaredMethod("existsDate", String.class, LocalDate.class);
            var parameters = method.getParameters();

            // Assert
            assertEquals(String.class, parameters[0].getType(), 
                "Should accept enterpriseId as first parameter");
            assertTrue(parameters[0].isAnnotationPresent(PathVariable.class), 
                "EnterpriseId should be part of the URL path");
        }
    }
}

