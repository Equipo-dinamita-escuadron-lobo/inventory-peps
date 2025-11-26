package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.multitenancy.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.messageBroker.aspect.JwtTokenService;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.multitenancy.utils.TenantContext;

public class TenantInterceptor implements WebRequestInterceptor{

   @Autowired
    private JwtTokenService jwtTokenService;

    /**
     * @brief Pre-handle method called before the controller execution.
     * 
     * Sets the tenant identifier from the JWT into the TenantContext.
     * Uses the unified service that handles both HTTP and RabbitMQ contexts.
     * 
     * @param request The web request
     * @throws Exception If the tenant identifier could not be set from the JWT
     */
    @Override
    public void preHandle(WebRequest request) throws Exception {
        try {
            String tenantId = jwtTokenService.getTenantId();
            TenantContext.setTenantId(tenantId);
        } catch (Exception e) {
            // In case of error, do not set the tenant context
            // This allows the application to function without tenant context if necessary
            throw new Exception("No se pudo establecer el contexto del tenant desde el JWT", e);
        }
    }

    /**  
     * @brief Post-handle method called after the controller execution.
     * 
     * Clears the tenant identifier from the TenantContext to prevent context leakage.
     * 
     * @param request The web request
     * @param model The model map
     * @throws Exception If an error occurs
     */
    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        TenantContext.clear();
    }

    /**
     * @brief Callback after request completion.
     * 
     * Called after the handler and postHandle. No action required here,
     * but implemented to satisfy WebRequestInterceptor interface.
     * 
     * @param request The web request
     * @param ex The exception thrown by the handler, if any
     * @throws Exception If an unexpected error occurs
     */
    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
        // Nothing to do here
    }
}
