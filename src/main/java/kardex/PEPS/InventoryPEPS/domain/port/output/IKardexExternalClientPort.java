package kardex.PEPS.InventoryPEPS.domain.port.output;

import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;

/**
 * @brief Output port for external kardex system integration
 * 
 * Provides interface for retrieving kardex information
 * from external kardex management systems.
 */
public interface IKardexExternalClientPort {
       /**
     * @brief Retrieves kardex records by enterprise ID
     * @param enterpriseId Enterprise identifier
     * @return List of kardex records for the enterprise
     */
    List<Kardex> findKardexByEnterpriseId(String enterpriseId);
}
