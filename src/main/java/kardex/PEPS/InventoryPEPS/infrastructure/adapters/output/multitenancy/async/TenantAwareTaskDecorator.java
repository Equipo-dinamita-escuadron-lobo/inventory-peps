package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.multitenancy.async;

import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.multitenancy.utils.TenantContext;

public class TenantAwareTaskDecorator implements TaskDecorator{

    /**
     * @brief Decorates a given Runnable to ensure the current tenant context is preserved.
     * 
     * Sets the tenant context before the Runnable executes and clears it after execution.
     * This ensures that tenant-specific data is handled correctly in asynchronous tasks.
     *
     * @param runnable The original Runnable to be decorated
     * @return A new Runnable that wraps the execution with tenant context management
     */
    @Override
     @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
         String tenantId = TenantContext.getTenantId();
        return () -> {
            try {
                TenantContext.setTenantId(tenantId);
                runnable.run();
            } finally {
                TenantContext.clear();
            }
        };
    }
    
}
