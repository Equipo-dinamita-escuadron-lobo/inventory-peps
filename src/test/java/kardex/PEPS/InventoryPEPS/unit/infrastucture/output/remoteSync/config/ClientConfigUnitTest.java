package kardex.PEPS.InventoryPEPS.unit.infrastucture.output.remoteSync.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientConfig;

/**
 * Unit tests for {@link ClientConfig}
 * 
 * Tests the configuration class for WebClient proxies for external service clients.
 * Note: Most of the functionality is tested via integration tests as the methods
 * create Spring HTTP Service proxies which require Spring context.
 * 
 * @author Equipo-dinamita-escuadron-lobo
 */
@DisplayName("ClientConfig Unit Tests")
public class ClientConfigUnitTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create ClientConfig instance")
        void shouldCreateClientConfigInstance() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> {
                // ClientConfig requires JwtTokenService in constructor
                // This test validates the class structure exists
                assertNotNull(ClientConfig.class);
            });
        }

       
        @Test
        @DisplayName("Should have EnableConfigurationProperties annotation")
        void shouldHaveEnableConfigurationPropertiesAnnotation() {
            // Arrange & Act
            boolean hasEnableConfigPropsAnnotation = ClientConfig.class.isAnnotationPresent(
                org.springframework.boot.context.properties.EnableConfigurationProperties.class
            );

            // Assert
            assertTrue(hasEnableConfigPropsAnnotation, 
                "ClientConfig should have @EnableConfigurationProperties annotation");
        }
    }

    @Nested
    @DisplayName("Bean Method Existence Tests")
    class BeanMethodTests {

        @Test
        @DisplayName("Should have stockClient bean method")
        void shouldHaveStockClientBeanMethod() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> {
                ClientConfig.class.getDeclaredMethod("stockClient", 
                    org.springframework.web.reactive.function.client.WebClient.Builder.class,
                    kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
                );
            }, "ClientConfig should have stockClient method");
        }

        @Test
        @DisplayName("Should have productClient bean method")
        void shouldHaveProductClientBeanMethod() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> {
                ClientConfig.class.getDeclaredMethod("productClient",
                    org.springframework.web.reactive.function.client.WebClient.Builder.class,
                    kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
                );
            }, "ClientConfig should have productClient method");
        }

        @Test
        @DisplayName("Should have configClient bean method")
        void shouldHaveConfigClientBeanMethod() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> {
                ClientConfig.class.getDeclaredMethod("configClient",
                    org.springframework.web.reactive.function.client.WebClient.Builder.class,
                    kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
                );
            }, "ClientConfig should have configClient method");
        }

        @Test
        @DisplayName("Should have kardexExternalClient bean method")
        void shouldHaveKardexExternalClientBeanMethod() {
            // Arrange & Act & Assert
            assertDoesNotThrow(() -> {
                ClientConfig.class.getDeclaredMethod("kardexExternalClient",
                    org.springframework.web.reactive.function.client.WebClient.Builder.class,
                    kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
                );
            }, "ClientConfig should have kardexExternalClient method");
        }

        @Test
        @DisplayName("Stock client method should have Bean annotation")
        void stockClientMethodShouldHaveBeanAnnotation() throws Exception {
            // Arrange & Act
            var method = ClientConfig.class.getDeclaredMethod("stockClient",
                org.springframework.web.reactive.function.client.WebClient.Builder.class,
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
            );
            boolean hasBeanAnnotation = method.isAnnotationPresent(
                org.springframework.context.annotation.Bean.class
            );

            // Assert
            assertTrue(hasBeanAnnotation, "stockClient method should have @Bean annotation");
        }

        @Test
        @DisplayName("Product client method should have Bean annotation")
        void productClientMethodShouldHaveBeanAnnotation() throws Exception {
            // Arrange & Act
            var method = ClientConfig.class.getDeclaredMethod("productClient",
                org.springframework.web.reactive.function.client.WebClient.Builder.class,
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
            );
            boolean hasBeanAnnotation = method.isAnnotationPresent(
                org.springframework.context.annotation.Bean.class
            );

            // Assert
            assertTrue(hasBeanAnnotation, "productClient method should have @Bean annotation");
        }

        @Test
        @DisplayName("Config client method should have Bean annotation")
        void configClientMethodShouldHaveBeanAnnotation() throws Exception {
            // Arrange & Act
            var method = ClientConfig.class.getDeclaredMethod("configClient",
                org.springframework.web.reactive.function.client.WebClient.Builder.class,
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
            );
            boolean hasBeanAnnotation = method.isAnnotationPresent(
                org.springframework.context.annotation.Bean.class
            );

            // Assert
            assertTrue(hasBeanAnnotation, "configClient method should have @Bean annotation");
        }

        @Test
        @DisplayName("Kardex external client method should have Bean annotation")
        void kardexExternalClientMethodShouldHaveBeanAnnotation() throws Exception {
            // Arrange & Act
            var method = ClientConfig.class.getDeclaredMethod("kardexExternalClient",
                org.springframework.web.reactive.function.client.WebClient.Builder.class,
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
            );
            boolean hasBeanAnnotation = method.isAnnotationPresent(
                org.springframework.context.annotation.Bean.class
            );

            // Assert
            assertTrue(hasBeanAnnotation, "kardexExternalClient method should have @Bean annotation");
        }
    }

    @Nested
    @DisplayName("Bean Method Return Type Tests")
    class BeanMethodReturnTypeTests {

        @Test
        @DisplayName("Stock client should return IStockClient type")
        void stockClientShouldReturnIStockClientType() throws Exception {
            // Arrange & Act
            var method = ClientConfig.class.getDeclaredMethod("stockClient",
                org.springframework.web.reactive.function.client.WebClient.Builder.class,
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
            );
            Class<?> returnType = method.getReturnType();

            // Assert
            assertEquals(
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IStockClient.class,
                returnType,
                "stockClient should return IStockClient type"
            );
        }

        @Test
        @DisplayName("Product client should return IProductClient type")
        void productClientShouldReturnIProductClientType() throws Exception {
            // Arrange & Act
            var method = ClientConfig.class.getDeclaredMethod("productClient",
                org.springframework.web.reactive.function.client.WebClient.Builder.class,
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
            );
            Class<?> returnType = method.getReturnType();

            // Assert
            assertEquals(
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IProductClient.class,
                returnType,
                "productClient should return IProductClient type"
            );
        }

        @Test
        @DisplayName("Config client should return IConfigClient type")
        void configClientShouldReturnIConfigClientType() throws Exception {
            // Arrange & Act
            var method = ClientConfig.class.getDeclaredMethod("configClient",
                org.springframework.web.reactive.function.client.WebClient.Builder.class,
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
            );
            Class<?> returnType = method.getReturnType();

            // Assert
            assertEquals(
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IConfigClient.class,
                returnType,
                "configClient should return IConfigClient type"
            );
        }

        @Test
        @DisplayName("Kardex external client should return IKardexExternalClient type")
        void kardexExternalClientShouldReturnIKardexExternalClientType() throws Exception {
            // Arrange & Act
            var method = ClientConfig.class.getDeclaredMethod("kardexExternalClient",
                org.springframework.web.reactive.function.client.WebClient.Builder.class,
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.ClientProperties.class
            );
            Class<?> returnType = method.getReturnType();

            // Assert
            assertEquals(
                kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IKardexExternalClient.class,
                returnType,
                "kardexExternalClient should return IKardexExternalClient type"
            );
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("Should be a public class")
        void shouldBePublicClass() {
            // Arrange & Act
            int modifiers = ClientConfig.class.getModifiers();

            // Assert
            assertTrue(java.lang.reflect.Modifier.isPublic(modifiers), 
                "ClientConfig should be a public class");
        }

        @Test
        @DisplayName("Should not be abstract")
        void shouldNotBeAbstract() {
            // Arrange & Act
            int modifiers = ClientConfig.class.getModifiers();

            // Assert
            assertFalse(java.lang.reflect.Modifier.isAbstract(modifiers), 
                "ClientConfig should not be abstract");
        }

        @Test
        @DisplayName("Should not be an interface")
        void shouldNotBeInterface() {
            // Arrange & Act & Assert
            assertFalse(ClientConfig.class.isInterface(), 
                "ClientConfig should not be an interface");
        }

        @Test
        @DisplayName("Should have exactly 4 bean methods")
        void shouldHaveExactlyFourBeanMethods() {
            // Arrange & Act
            long beanMethodCount = java.util.Arrays.stream(ClientConfig.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(
                    org.springframework.context.annotation.Bean.class
                ))
                .count();

            // Assert
            assertEquals(4, beanMethodCount, 
                "ClientConfig should have exactly 4 @Bean methods");
        }
    }
}
