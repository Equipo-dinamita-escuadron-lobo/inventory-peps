package kardex.PEPS.InventoryPEPS.domain.port.output;

import java.util.List;
import kardex.PEPS.InventoryPEPS.domain.model.DetailOutput;

public interface IDetailQueryOutPutPort {

    List<DetailOutput> findByMovementSale(Long idMovementsale);
    void deleteById(Long id);

    List<DetailOutput> findByMovementSaleOrderedDesc(Long idKardexSale);
    DetailOutput update(DetailOutput detail);
    
}
