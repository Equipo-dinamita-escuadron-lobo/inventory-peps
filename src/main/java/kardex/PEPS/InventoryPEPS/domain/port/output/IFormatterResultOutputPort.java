package kardex.PEPS.InventoryPEPS.domain.port.output;

public interface IFormatterResultOutputPort {
    public void returnBusinessRuleErrorResponse(int status, String message);
    public void returnEntityAlreadyExistsErrorResponse(int status, String message);
    public void returnEntityDoesNotExistErrorResponse(int status, String message);
    public void returnErrorGenericResponse(int status, String message);
}
