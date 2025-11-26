package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.multitenancy.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContext {
    private TenantContext() {
    }

    private static final InheritableThreadLocal<String> currentTenant = new InheritableThreadLocal<>();
    
    /**
     * @brief Sets the tenant identifier in the current thread context.
     * 
     * Typically called by {@link TenantInterceptor} before the controller is invoked.
     * 
     * @param tenantId The tenant identifier
     */
    public static void setTenantId(String tenantId) {
        log.debug("Setting tenantId to " + tenantId);
        currentTenant.set(tenantId);
    }

    /**
     * @brief Gets the tenant identifier from the current thread context.
     * 
     * @return The tenant identifier, or null if none has been set
     */
    public static String getTenantId() {
        return currentTenant.get();
    }

    /**
     * @brief Clears the tenant identifier from the current thread context.
     * 
     * Typically called after the controller execution is complete.
     */
    public static void clear() {
        currentTenant.remove();
    }

}
