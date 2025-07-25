package kardex.PEPS.InventoryPEPS.application.service;

import org.springframework.stereotype.Service;

import kardex.PEPS.InventoryPEPS.application.ports.input.IKardexCommandPort;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexCommandOutputPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KardexCommandService implements IKardexCommandPort {
    private final IKardexCommandOutputPort kardexCommandOutputPort;
    

    @Override
    public Kardex registerPurchase(Kardex kardex) {
        throw new UnsupportedOperationException("Unimplemented method 'registerSale'");
        
    }

    @Override
    public Kardex registerSale(Kardex kardex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerSale'");
    }

    @Override
    public Kardex registerPurchaseReturn(Kardex kardex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerPurchaseReturn'");
    }

    @Override
    public Kardex registerSaleReturn(Kardex kardex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerSaleReturn'");
    }
    
}
