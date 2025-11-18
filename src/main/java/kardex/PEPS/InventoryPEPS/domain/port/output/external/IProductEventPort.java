package kardex.PEPS.InventoryPEPS.domain.port.output.external;

public interface IProductEventPort {
    void publishUsedProductEvent(Long productId, Integer quantityUsed);

}
