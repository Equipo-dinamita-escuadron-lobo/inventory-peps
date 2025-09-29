package kardex.PEPS.InventoryPEPS.application.ports.input;

import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;

public interface  IKardexCommandPort {
     Kardex registerPurchase(Kardex kardex);
     Kardex registerSale(Kardex kardex);
     Kardex registerPurchaseReturn(Kardex kardex);
     List<Kardex> registerSaleReturn(Kardex kardex);
     Kardex registerNonCommercialExit(Kardex kardex);
     Kardex registerNonCommercialEntry(Kardex kardex);

    
}
