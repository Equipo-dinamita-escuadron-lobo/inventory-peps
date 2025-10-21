package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.adapter;

import java.util.List;
import org.springframework.stereotype.Component;
import kardex.PEPS.InventoryPEPS.domain.model.DetailOutput;
import kardex.PEPS.InventoryPEPS.domain.port.output.IDetailQueryOutPutPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper.IDetailOutPutSaleReturnMapper;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IDetailOutPutRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DetailOutputQueryAdapter implements IDetailQueryOutPutPort {

    private final IDetailOutPutRepository detailOutPutRepository;
    private final IDetailOutPutSaleReturnMapper detailOutPutSaleReturnMapper;

    @Override
    public List<DetailOutput>  findByMovementSale(Long idMovementsale) {
        return detailOutPutSaleReturnMapper.toDomainList(detailOutPutRepository.findByMovementSaleId(idMovementsale));
    }

    @Override
    public void deleteById(Long id) {
        detailOutPutRepository.deleteById(id);
    }

    @Override
    public List<DetailOutput> findByMovementSaleOrderedDesc(Long idKardexSale) {
         
        List<DetailOutputEntity> entities = detailOutPutRepository.findByMovementSaleOrderedDesc(idKardexSale);
        return detailOutPutSaleReturnMapper.toDomainList(entities);
        
    }

    @Override
    public DetailOutput update(DetailOutput detail) {
       DetailOutputEntity entity = detailOutPutSaleReturnMapper.toEntity(detail);
        DetailOutputEntity saved = detailOutPutRepository.save(entity);
        return detailOutPutSaleReturnMapper.toDomain(saved);
    }
        
    
}
