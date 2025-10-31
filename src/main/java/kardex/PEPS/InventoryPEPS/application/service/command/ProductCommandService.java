package kardex.PEPS.InventoryPEPS.application.service.command;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import kardex.PEPS.InventoryPEPS.application.ports.input.IProductCommandPort;
import kardex.PEPS.InventoryPEPS.application.ports.input.IProductSyncCommandPort;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.model.SyncState;
import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IMessageServicePort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IProductClientPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IProductCommandOutPutPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.ISyncStateRepositoryPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.i18n.MessageKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductCommandService implements IProductSyncCommandPort,IProductCommandPort {
    private final IProductCommandOutPutPort productCommandOutPutPort;
    private final IProductClientPort productClient;
    private final ISyncStateRepositoryPort syncStateRepository;
    private final IFormatterResultOutputPort formatterResultOutputPort;
    private final IMessageServicePort messageService;
    
    private static final String SYNC_TYPE_PRODUCTS = "products";

    @Override
    public String syncProductsByEnterpriseId(String enterpriseId) {
        log.info(messageService.getMessage(MessageKeys.LOG_SYNC_STARTED, enterpriseId));

    
        try {
            //1. Obtener o crear estado de sincronización
            SyncState syncState = getOrCreateSyncState(enterpriseId);
            
            // Logging inteligente usando consultas de dominio
            logSyncStateInfo(syncState, enterpriseId);

            // Marcar inicio de sincronización
            syncState.markSyncStarted();
            Instant syncStartedAt = Instant.now();

            // Obtener productos actualizados desde la última sincronización
            List<Product> updatedProducts = fetchUpdatedProducts(enterpriseId, syncState.getLastSyncDate());

            // Procesar productos usando lógica de dominio
            ProductSyncResult syncResult = processUpdatedProducts(updatedProducts, enterpriseId);

            //Actualizar estado usando comportamiento de dominio
            updateSyncStateWithValidation(syncState, syncStartedAt);

            //Log y retorno del resultado
            log.info(messageService.getMessage(MessageKeys.LOG_SYNC_SUCCESS, enterpriseId, syncResult.processedCount()));
            return formatSuccessMessage(syncResult);

        } catch (Exception e) {
            log.error(messageService.getMessage(MessageKeys.LOG_SYNC_ERROR, enterpriseId), e);
            formatterResultOutputPort.returnBusinessRuleErrorResponse(500, 
                messageService.getMessage(MessageKeys.ERROR_SYNC_PRODUCTS, e.getMessage()));
            throw e;
        }
    }


    /**
     * Obtiene o crea un estado de sincronización
     */
    private SyncState getOrCreateSyncState(String enterpriseId) {
        Optional<SyncState> existingState = syncStateRepository
            .findBySyncTypeAndEnterpriseId(SYNC_TYPE_PRODUCTS, enterpriseId);

        if (existingState.isPresent()) {
            SyncState state = existingState.get();
            
            // Validar consistencia usando método de dominio
            try {
                state.validateDateConsistency();
            } catch (IllegalStateException e) {
                log.warn("Sync state inconsistency detected for enterprise {}: {}. Resetting state.", 
                    enterpriseId, e.getMessage());
                state.resetToInitialState();
                syncStateRepository.save(state);
            }
            
            return state;
        }

        //Crear nuevo estado 
        log.info(messageService.getMessage(MessageKeys.LOG_NO_PREVIOUS_SYNC, enterpriseId));
        SyncState newState = SyncState.createInitialProductSync(enterpriseId);
        syncStateRepository.save(newState);
        
        log.info(messageService.getMessage(MessageKeys.LOG_SYNC_STATE_CREATED, enterpriseId));
        return newState;
    }

    /**
     * Registra información del estado de sincronización usando consultas de dominio
     */
    private void logSyncStateInfo(SyncState syncState, String enterpriseId) {
        if (syncState.isFirstSync()) {
            log.info("Performing first synchronization for enterprise: {}", enterpriseId);
        } else {
            long minutesSinceLastSync = syncState.getMinutesSinceLastSync();
            log.info(messageService.getMessage(MessageKeys.LOG_LAST_SYNC_DATE, 
                enterpriseId, syncState.getLastSyncDate()));
            
            // Alertas inteligentes usando lógica de dominio
            if (syncState.needsSyncAfterMinutes(60)) {
                log.warn("Sync state for enterprise {} is outdated ({} minutes old)", 
                    enterpriseId, minutesSinceLastSync);
            }
            
            if (syncState.wasUpdatedInLastMinutes(5)) {
                log.info("Recent sync activity detected for enterprise {}", enterpriseId);
            }
        }
    }

    /**
     * Obtiene productos actualizados del cliente externo
     */
    private List<Product> fetchUpdatedProducts(String enterpriseId, Instant lastSyncDate) {
        try {
            return productClient.findAllProductsByEnterpriseId(enterpriseId, lastSyncDate);
        } catch (Exception e) {
            log.error("Error fetching products from external service for enterprise: {}", enterpriseId, e);
            throw new RuntimeException("Failed to fetch products from external service", e);
        }
    }

    /**
     * Procesa los productos actualizados 
     */
    private ProductSyncResult processUpdatedProducts(List<Product> products, String enterpriseId) {
        if (products == null || products.isEmpty()) {
            log.info(messageService.getMessage(MessageKeys.LOG_NO_PRODUCTS_TO_PROCESS, enterpriseId));
            return new ProductSyncResult(0, 0, 0);
        }

        try {
            //Convertir DTOs a productos válidos
            List<Product> validatedProducts = convertToValidatedProducts(products, enterpriseId);
            
            if (validatedProducts.isEmpty()) {
                log.warn("No valid products found after conversion for enterprise: {}", enterpriseId);
                return new ProductSyncResult(0, 0, validatedProducts.size() - products.size());
            }
            
            //  Separar productos por operación
            ProductOperationResult operationResult = categorizeProducts(validatedProducts);
            
            //Guardar productos
            String result = productCommandOutPutPort.saveAll(operationResult.allProducts());
            log.info(messageService.getMessage(MessageKeys.LOG_PRODUCTS_SAVED, result));
            
            return new ProductSyncResult(
                operationResult.allProducts().size(),
                operationResult.newProducts().size(),
                operationResult.updatedProducts().size()
            );
            
        } catch (Exception e) {
            log.error(messageService.getMessage(MessageKeys.LOG_PROCESSING_PRODUCTS_ERROR, enterpriseId), e);
            throw new RuntimeException("Failed to process products", e);
        }
    }

    /**
     * Convierte DTOs a productos válidos usando 
     */
    private List<Product> convertToValidatedProducts(List<Product> productDTOs, String enterpriseId) {
        return productDTOs.stream()
            .map(dto -> convertSingleProduct(dto, enterpriseId))
            .filter(Objects::nonNull) 
            .toList();
    }

    /**
     * Convierte un producto individual y validaciones de dominio
     */
    private Product convertSingleProduct(Product dto, String enterpriseId) {
        try {
            
            Product product = Product.create(
                dto.getProductId(),
                dto.getName(),
                dto.getReference(),
                dto.getPresentation(),
                enterpriseId
            );
            
            //  Establecer estado usando comportamiento de dominio
            if (!dto.isState()) {
                product.deactivate();
            }
            
            return product;
            
        } catch (Exception e) {
            log.warn("Failed to convert product with ID: {} for enterprise: {} - {}", 
                dto.getProductId(), enterpriseId, e.getMessage());
            return null;
        }
    }

    /**
     * Categoriza productos por tipo de operación
     */
    private ProductOperationResult categorizeProducts(List<Product> products) {
        //Implementación simplificada
        List<Product> newProducts = products.stream()
            .filter(p -> p.getId() == null)
            .toList();
            
        List<Product> updatedProducts = products.stream()
            .filter(p -> p.getId() != null)
            .toList();
            
        return new ProductOperationResult(products, newProducts, updatedProducts);
    }

    /**
     * Actualiza el estado de sincronización c
     */
    private void updateSyncStateWithValidation(SyncState syncState, Instant syncDate) {
        try {
            // Usar comportamiento de dominio para actualizar
            syncState.updateSyncDate(syncDate);
            syncState.validateForUpdate();
            syncState.validateDateConsistency();
            
            syncStateRepository.save(syncState);
            
        } catch (Exception e) {
            log.error("Failed to update sync state: {}", e.getMessage());
            throw new RuntimeException("Failed to update synchronization state", e);
        }
    }

    /**
     * Formatea el mensaje de éxito con información detallada
     */
    private String formatSuccessMessage(ProductSyncResult result) {
        if (result.newCount() > 0 && result.updatedCount() > 0) {
            return String.format("Successful synchronization: %d products processed (%d new, %d updated)",
                result.processedCount(), result.newCount(), result.updatedCount());
        } else if (result.newCount() > 0) {
            return String.format("Successful synchronization: %d new products processed",
                result.newCount());
        } else if (result.updatedCount() > 0) {
            return String.format("Successful synchronization: %d products updated",
                result.updatedCount());
        } else {
            return "Synchronization completed: No products to process";
        }
    }

    
    private record ProductSyncResult(int processedCount, int newCount, int updatedCount) {}
    
    private record ProductOperationResult(
        List<Product> allProducts, 
        List<Product> newProducts, 
        List<Product> updatedProducts
    ) {}

    @Override
    public String deleteById(Long productId, String enterpriseId) {
        log.info("Deleting product with ID {} for enterprise {}", productId, enterpriseId);
        return productCommandOutPutPort.deleteById(productId, enterpriseId); 
    }


    @Override
    public String deleteAllByEnterpriseId(String enterpriseId) {
        log.info("Deleting all products for enterprise {}", enterpriseId);
        return productCommandOutPutPort.deleteAllByEnterpriseId(enterpriseId);
    }


    
}
