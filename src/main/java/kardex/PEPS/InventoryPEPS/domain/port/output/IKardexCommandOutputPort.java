package kardex.PEPS.InventoryPEPS.domain.port.output;

import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;

public interface  IKardexCommandOutputPort {

     Kardex registerPurchase(Kardex kardex);
     Kardex registerSale(Kardex kardex,List<Kardex> lotsToUpdate);
     Kardex registerPurchaseReturn(Kardex kardex);
     Kardex registerSaleReturn(Kardex kardex);
     Kardex registerNonCommercialExit(Kardex kardex, List<Kardex> lotsToUpdate);
     Kardex registerNonCommercialEntry(Kardex kardex);
     int updateAvaliableAmount(Long idKardex, int newAmount);
     
     /**
     * @brief Deletes all kardex records
     */
     void deleteAll();
    
}
