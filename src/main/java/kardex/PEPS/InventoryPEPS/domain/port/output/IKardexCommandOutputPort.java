package kardex.PEPS.InventoryPEPS.domain.port.output;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;

public interface  IKardexCommandOutputPort {

     Kardex registerPurchase(Kardex kardex);
     Kardex registerSale(Kardex kardex);
     Kardex registerPurchaseReturn(Kardex kardex);
     Kardex registerSaleReturn(Kardex kardex);
    
    
}
