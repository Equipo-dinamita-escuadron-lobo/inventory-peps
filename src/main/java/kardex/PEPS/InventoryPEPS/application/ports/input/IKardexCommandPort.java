package kardex.PEPS.InventoryPEPS.application.ports.input;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;

public interface  IKardexCommandPort {
     Kardex registerPurchase(Kardex kardex);
     Kardex registerSale(Kardex kardex);
     Kardex registerPurchaseReturn(Kardex kardex);
     Kardex registerSaleReturn(Kardex kardex);
    
}
