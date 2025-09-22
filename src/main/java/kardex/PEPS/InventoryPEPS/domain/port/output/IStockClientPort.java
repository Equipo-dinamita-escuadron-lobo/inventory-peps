package kardex.PEPS.InventoryPEPS.domain.port.output;

import kardex.PEPS.InventoryPEPS.domain.model.Stock;

public interface IStockClientPort {
    void buyStock(Stock stock);
    void sellStock(Stock stock);
}
