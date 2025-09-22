package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import kardex.PEPS.InventoryPEPS.domain.model.DetailOutput;
import kardex.PEPS.InventoryPEPS.domain.port.output.IDetailQueryOutPutPort;
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
        
    
}
