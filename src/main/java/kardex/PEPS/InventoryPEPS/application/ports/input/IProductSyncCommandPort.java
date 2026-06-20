package kardex.PEPS.InventoryPEPS.application.ports.input;

/**
 * @brief Input port for Product synchronization command operations
 * 
 * Defines the contract for synchronizing product data for an enterprise
 * from external sources or internal reconciliation.
 */
public interface IProductSyncCommandPort {
     /**
     * @brief Synchronizes products for a specific enterprise
     * @param enterpriseId Enterprise identifier
     * @return Status message indicating synchronization result
     */
     String syncProductsByEnterpriseId(String enterpriseId);
}
