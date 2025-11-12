package kardex.PEPS.InventoryPEPS.domain.port.output;

public interface IProductEventPort {
    void publishUsedProductEvent(Long productId, Integer quantityUsed);

}
