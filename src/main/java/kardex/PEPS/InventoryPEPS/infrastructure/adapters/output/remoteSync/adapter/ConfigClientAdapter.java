package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.adapter;

import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.external.IConfigClientPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config.IConfigClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Adapter for Configuration Service communication
 * 
 * Implements the output port to interact with the external Configuration Service.
 * Handles the validation of accounting dates and manages communication errors.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigClientAdapter implements IConfigClientPort{
    private final IConfigClient configClient;
    private final IFormatterResultOutputPort formatterResultOutputPort;

    /**
     * @brief Validates if a date is within an allowed accounting period
     * 
     * Calls the external configuration service to check if the provided date
     * is valid for accounting operations for the specified enterprise.
     * Handles service unavailability and other communication errors.
     * 
     * @param enterpriseId The unique identifier of the enterprise
     * @param date The date to validate
     * @return true if the date is valid, false otherwise or if an error occurs
     */
    @Override
    public boolean isValidAccountingDate(String enterpriseId, LocalDate date) {
       
        try{

            return configClient.existsDate(enterpriseId, date);
        
        }catch (WebClientResponseException.ServiceUnavailable e) {
            log.warn("Configuration service is unavailable (503)");
            formatterResultOutputPort.returnErrorGenericResponse(503, "Configuration service is unavailable");
        } catch (WebClientResponseException e) {
            log.warn("Error calling configuration service: {} - {}", e.getStatusCode(), e.getStatusText());
            formatterResultOutputPort.returnErrorGenericResponse(500,"Error communicating with configuration service");
        } catch (Exception e) {
            log.warn("Unexpected error calling configuration service: {}", e.getMessage());
            formatterResultOutputPort.returnErrorGenericResponse(500,"Unexpected error communicating with configuration service");    
        }
        return false;
    }
    
}
