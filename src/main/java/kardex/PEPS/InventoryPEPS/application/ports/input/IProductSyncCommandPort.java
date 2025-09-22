package kardex.PEPS.InventoryPEPS.application.ports.input;

public interface IProductSyncCommandPort {
     String syncProductsByEnterpriseId(String enterpriseId);
}
