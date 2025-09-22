package kardex.PEPS.InventoryPEPS.application.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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
public class ProductCommandService implements IProductSyncCommandPort {
    private final IProductCommandOutPutPort productCommandOutPutPort;
    private final IProductClientPort productClient;
    private final ISyncStateRepositoryPort syncStateRepository;
    private final IFormatterResultOutputPort formatterResultOutputPort;
    private final IMessageServicePort messageService;
    
    private static final String SYNC_TYPE_PRODUCTS = "products";


    @Override
    public String syncProductsByEnterpriseId(String enterpriseId) {
        log.info(messageService.getMessage(MessageKeys.LOG_SYNC_STARTED, enterpriseId));

         // 1. Obtain the last synchronization date
        Optional<Instant> lastSync = syncStateRepository.findLastSyncFor(SYNC_TYPE_PRODUCTS, enterpriseId);

        if (lastSync.isEmpty()) {
            // If no previous sync exists, create a new sync state with the current time
            Instant oldDate = Instant.parse("2000-01-01T00:00:00Z");
            lastSync = createSyncStateIfNotExists(enterpriseId, oldDate); 
            log.info(messageService.getMessage(MessageKeys.LOG_NO_PREVIOUS_SYNC, enterpriseId));
        }

        log.info(messageService.getMessage(MessageKeys.LOG_LAST_SYNC_DATE, enterpriseId, lastSync.get()));

        // 2. Moment before the call (this will be the new date if all goes well)
        Instant syncStartedAt = Instant.now();

        try {
            // 3. Call the API
            List<Product> updatedProducts = productClient.findAllProductsByEnterpriseId(enterpriseId, lastSync.get());

            // 4. Process the received products
            processUpdatedProducts(updatedProducts, enterpriseId);

            // 5. Only if everything was successful, update the date
            updateSyncState(enterpriseId, syncStartedAt);

            log.info(messageService.getMessage(MessageKeys.LOG_SYNC_SUCCESS, enterpriseId, updatedProducts.size()));

            return "Successful synchronization: " + updatedProducts.size() + " products processed.";

        } catch (Exception e) {
            log.error(messageService.getMessage(MessageKeys.LOG_SYNC_ERROR, enterpriseId), e);
            formatterResultOutputPort.returnBusinessRuleErrorResponse(500, 
                messageService.getMessage(MessageKeys.ERROR_SYNC_PRODUCTS, e.getMessage()));
            throw e;
        }

    }

    private void processUpdatedProducts(List<Product> products, String enterpriseId) {
        try {
            if (products == null || products.isEmpty()) {
                log.info(messageService.getMessage(MessageKeys.LOG_NO_PRODUCTS_TO_PROCESS, enterpriseId));
                return;
            }
            
            List<Product> productList = getProductsFromDto(products, enterpriseId);

            // Save all products to the database
            String result = productCommandOutPutPort.saveAll(productList);
            log.info(messageService.getMessage(MessageKeys.LOG_PRODUCTS_SAVED, result));
        } catch (Exception e) {
            log.error(messageService.getMessage(MessageKeys.LOG_PROCESSING_PRODUCTS_ERROR, enterpriseId), e);
        }
    }
    
    private List<Product> getProductsFromDto(List<Product> products, String enterpriseId) {
        return products.stream()
            .map(dto -> Product.builder()
                .productId(dto.getProductId())
                .reference(dto.getReference())
                .name(dto.getName())
                .presentation(dto.getPresentation())
                .manager(null)
                .enterpriseId(enterpriseId)
                .state(dto.isState())
                .build())
            .toList();
    }

    private void updateSyncState(String enterpriseId, Instant syncDate) {
        Optional<SyncState> existingState = syncStateRepository
            .findBySyncTypeAndEnterpriseId(SYNC_TYPE_PRODUCTS, enterpriseId);
        
        if (existingState.isPresent()) {
            // Update existing record
            SyncState state = existingState.get();
            state.setLastSyncDate(syncDate);
            syncStateRepository.save(state);
        } else {
            // Create new record
            SyncState newState = new SyncState();
            newState.setSyncType(SYNC_TYPE_PRODUCTS);
            newState.setEnterpriseId(enterpriseId);
            newState.setLastSyncDate(syncDate);
            syncStateRepository.save(newState);
        }
    }

    private Optional<Instant> createSyncStateIfNotExists(String enterpriseId, Instant syncDate) {
        SyncState newState = new SyncState();
        newState.setSyncType(SYNC_TYPE_PRODUCTS);
        newState.setEnterpriseId(enterpriseId);
        newState.setLastSyncDate(syncDate);
        syncStateRepository.save(newState);
        log.info(messageService.getMessage(MessageKeys.LOG_SYNC_STATE_CREATED, enterpriseId));
        return Optional.of(syncDate);
    }


    
}
