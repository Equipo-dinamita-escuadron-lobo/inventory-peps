package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.adapter;

import org.springframework.stereotype.Component;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexCommandOutputPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.ProductEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper.IKardexEntityCommandMapper;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IKardexRepository;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IProductRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KardexCommandAdapter implements IKardexCommandOutputPort {
    private final IKardexEntityCommandMapper kardexEntityCommandMapper;
    private final IKardexRepository kardexRepository;
    private final IProductRepository productRepository;

    @Override
    public Kardex registerPurchase(Kardex kardex) {
        ProductEntity productEntity=productRepository.getReferenceById(kardex.getObjProduct().getId());
        KardexEntity kardexEntity=kardexEntityCommandMapper.toEntity(kardex);
        kardexEntity.setObjProduct(productEntity);
       return kardexEntityCommandMapper.toDomain(kardexRepository.save(kardexEntity));
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
