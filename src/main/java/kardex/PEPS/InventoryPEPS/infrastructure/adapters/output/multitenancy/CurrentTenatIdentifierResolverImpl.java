package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.multitenancy;


import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.multitenancy.utils.TenantContext;

import java.util.Map;


@SuppressWarnings("rawtypes")
@Component
public class CurrentTenatIdentifierResolverImpl  implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {

     /**
     * @brief Resolves the current tenant identifier.
     * 
     * @return The current tenant identifier from TenantContext if available;
     *         otherwise returns "BOOTSTRAP" to allow EntityManagerFactory initialization.
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getTenantId();
        if (!ObjectUtils.isEmpty(tenantId)) {
            return tenantId;
        } else {
            // Allow bootstrapping the EntityManagerFactory, in which case no tenant is
            // needed
            return "BOOTSTRAP";
        }
    }

    
    /**
     * @brief Validates existing current sessions.
     * 
     * @return true always, as current sessions are considered valid by default.
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    /**
     * @brief Customizes the given Hibernate properties.
     * 
     * Adds the current tenant identifier resolver as the value for the
     * {@link AvailableSettings#MULTI_TENANT_IDENTIFIER_RESOLVER} setting.
     * 
     * @param hibernateProperties The properties to customize
     */
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
    
}
