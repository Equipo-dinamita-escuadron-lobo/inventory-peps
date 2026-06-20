package kardex.PEPS.InventoryPEPS.domain.port.output;

/**
 * @brief Output port for formatting and returning standardized responses
 * 
 * Defines methods for handling various types of error responses
 * and business rule violations.
 */
public interface IFormatterResultOutputPort {
    /**
     * @brief Returns a response for business rule violations
     * @param status HTTP status code
     * @param message Error message
     */
    public void returnBusinessRuleErrorResponse(int status, String message);

    /**
     * @brief Returns a response when an entity already exists
     * @param status HTTP status code
     * @param message Error message
     */
    public void returnEntityAlreadyExistsErrorResponse(int status, String message);

    /**
     * @brief Returns a response when an entity does not exist
     * @param status HTTP status code
     * @param message Error message
     */
    public void returnEntityDoesNotExistErrorResponse(int status, String message);

    /**
     * @brief Returns a generic error response
     * @param status HTTP status code
     * @param message Error message
     */
    public void returnErrorGenericResponse(int status, String message);
}
