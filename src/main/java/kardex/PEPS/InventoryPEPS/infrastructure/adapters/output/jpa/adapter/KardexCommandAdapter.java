package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import kardex.PEPS.InventoryPEPS.domain.model.DetailOutput;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexCommandOutputPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.ProductEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper.IKardexEntityCommandMapper;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IDetailOutPutRepository;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IKardexRepository;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IProductRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KardexCommandAdapter implements IKardexCommandOutputPort {
    private final IKardexEntityCommandMapper kardexEntityCommandMapper;
    private final IKardexRepository kardexRepository;
    private final IDetailOutPutRepository detailOutPutRepository;
    private final IProductRepository productRepository;

    @Override
    public Kardex registerPurchase(Kardex kardex) {
        ProductEntity productEntity=productRepository.getReferenceById(kardex.getProduct().getId());
        KardexEntity kardexEntity=kardexEntityCommandMapper.toEntity(kardex);
        kardexEntity.setProduct(productEntity);
       return kardexEntityCommandMapper.toDomain(kardexRepository.save(kardexEntity));
    }

    @Override
    @Transactional
    public Kardex registerSale(Kardex kardex, List<Kardex> lotsToUpdate) {
        ProductEntity productEntity=productRepository.getReferenceById(kardex.getProduct().getId());



        //actualizar el lote
        for(Kardex domainLot:lotsToUpdate){
            KardexEntity entityToUpdate=kardexEntityCommandMapper.toEntity(domainLot);
            entityToUpdate.setProduct(productEntity);
            kardexRepository.save(entityToUpdate);
        }

        //Guardamos primero el kardex de la venta
        KardexEntity kardexEntity=kardexEntityCommandMapper.toEntity(kardex);
        kardexEntity.setProduct(productEntity);
        kardexEntity=kardexRepository.save(kardexEntity);


        //Guardar detalles de la venta
        for(DetailOutput detail:kardex.getDetailsOutput()){
            DetailOutputEntity detailEntity=new DetailOutputEntity();
            detailEntity.setAmountUsed(detail.getAmountUsed());
            detailEntity.setUnitPrice(detail.getUnitPrice());

            //movimientos de venta
            detailEntity.setMovementSale(kardexEntity);

            // Usar el ID del movimiento de origen desde la referencia del objeto
            KardexEntity originEntity=kardexRepository.getReferenceById(detail.getMovementOrigin().getIdKardex());
            detailEntity.setMovementOrigin(originEntity);

            detailOutPutRepository.save(detailEntity);

        }
       
        return kardexEntityCommandMapper.toDomain(kardexEntity);
    }

    @Override
    @Transactional
    public Kardex registerPurchaseReturn(Kardex kardex){
        
        ProductEntity productEntity=productRepository.getReferenceById(kardex.getProduct().getId());
        KardexEntity kardexEntity=kardexEntityCommandMapper.toEntity(kardex);
        kardexEntity.setProduct(productEntity);
        return kardexEntityCommandMapper.toDomain(kardexRepository.save(kardexEntity));
        
    }

    @Override
    @Transactional
    public Kardex registerSaleReturn(Kardex kardex) {
        
        ProductEntity productEntity=productRepository.getReferenceById(kardex.getProduct().getId());
        KardexEntity kardexEntity=kardexEntityCommandMapper.toEntity(kardex);
        kardexEntity.setProduct(productEntity);
        return kardexEntityCommandMapper.toDomain(kardexRepository.save(kardexEntity));
    }

    @Override
    public int updateAvaliableAmount(Long idKardex, int newAmount) {
        return kardexRepository.updateAvaliableAmount(idKardex, newAmount);
    }

    @Override
    public Kardex registerNonCommercialExit(Kardex kardex,List<Kardex> lotsToUpdate) {
       
         ProductEntity productEntity=productRepository.getReferenceById(kardex.getProduct().getId());



        //actualizar el lote
        for(Kardex domainLot:lotsToUpdate){
            KardexEntity entityToUpdate=kardexEntityCommandMapper.toEntity(domainLot);
            entityToUpdate.setProduct(productEntity);
            kardexRepository.save(entityToUpdate);
        }

        //Guardamos primero el kardex de la venta
        KardexEntity kardexEntity=kardexEntityCommandMapper.toEntity(kardex);
        kardexEntity.setProduct(productEntity);
        kardexEntity=kardexRepository.save(kardexEntity);


        //Guardar detalles de la venta
        for(DetailOutput detail:kardex.getDetailsOutput()){
            DetailOutputEntity detailEntity=new DetailOutputEntity();
            detailEntity.setAmountUsed(detail.getAmountUsed());
            detailEntity.setUnitPrice(detail.getUnitPrice());

            //movimientos de venta
            detailEntity.setMovementSale(kardexEntity);

            // Usar el ID del movimiento de origen desde la referencia del objeto
            KardexEntity originEntity=kardexRepository.getReferenceById(detail.getMovementOrigin().getIdKardex());
            detailEntity.setMovementOrigin(originEntity);

            detailOutPutRepository.save(detailEntity);

        }
       
        return kardexEntityCommandMapper.toDomain(kardexEntity);

    }

    @Override
    public Kardex registerNonCommercialEntry(Kardex kardex) {
        ProductEntity productEntity=productRepository.getReferenceById(kardex.getProduct().getId());
        KardexEntity kardexEntity=kardexEntityCommandMapper.toEntity(kardex);
        kardexEntity.setProduct(productEntity);
       return kardexEntityCommandMapper.toDomain(kardexRepository.save(kardexEntity));
    }
    
   

}
