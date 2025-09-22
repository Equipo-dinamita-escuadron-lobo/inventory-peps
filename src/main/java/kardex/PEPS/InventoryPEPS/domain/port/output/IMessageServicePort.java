package kardex.PEPS.InventoryPEPS.domain.port.output;

public interface  IMessageServicePort {
     public String getMessage(String key, Object... args);
    public String getMessage(String key, String defaultMessage, Object... args);
}
