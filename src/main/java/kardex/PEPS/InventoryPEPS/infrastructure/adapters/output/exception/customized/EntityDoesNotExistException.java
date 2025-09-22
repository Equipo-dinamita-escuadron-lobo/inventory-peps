package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.exception.customized;

public class EntityDoesNotExistException extends BaseException {
    public EntityDoesNotExistException(Integer status,String message){
        super(status,message);
    }
}
