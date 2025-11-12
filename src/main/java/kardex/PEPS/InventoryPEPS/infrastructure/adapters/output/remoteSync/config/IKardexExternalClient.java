package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.KardexExternalResponseDTO;

public interface IKardexExternalClient {
    
     /**
     * @brief Retrieves kardex records by enterprise ID
     * @param enterpriseId The enterprise identifier
     * @return Response with list of kardex records
     */
    @GetExchange("/api/kardex/weighted-average/last-kardex-all-products/{enterpriseId}")
    KardexExternalResponseDTO findKardexByEnterpriseId(@PathVariable String enterpriseId);
}
