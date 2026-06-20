package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.config;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

/**
 * @brief HTTP client interface for Configuration Service
 * 
 * Defines the contract for interacting with the external Configuration Service
 * using Spring 6 HTTP Interfaces.
 */
public interface IConfigClient {
    
    /**
     * @brief Checks if a date is valid for accounting operations
     * 
     * @param enterpriseId The enterprise identifier
     * @param date The date to validate
     * @return true if the date exists/is valid in the accounting calendar
     */
    @GetExchange("/api/config/accounting-calendar/exists/{enterpriseId}")
    boolean existsDate(
        @PathVariable String enterpriseId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );
}
