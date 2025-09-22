package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.adapter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.port.output.IKardexQueryOutputPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.mapper.IKardexEntityQueryMapper;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IKardexRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KardexQueryAdapter implements IKardexQueryOutputPort{
    private final IKardexEntityQueryMapper kardexEntityQueryMapper;
    private final IKardexRepository kardexRepository;

    @Override
    public List<Kardex> getAllKardex(Long idProduct) {
        List<KardexEntity> listKardex=kardexRepository.findByProductId(idProduct);
        return kardexEntityQueryMapper.toDomainList(listKardex);
    }

    @Override
    public int getAvailableAmountByProduct(Long idProduct) {
       return  kardexRepository.sumAvailableAmountByProduct(idProduct);
    }

    @Override
    public Optional<Kardex> getFirstRecordByAmountAvailable(Long idProduct) {
        
        return kardexRepository.findFirstByAvailableAmount(idProduct)
        .map(kardexEntityQueryMapper::toDomain);
    }

    @Override
    public List<Kardex> findMovementsByProductAndDateRange(Long productId, LocalDate startDate, LocalDate endDate) {
        ZonedDateTime startDateTime=startDate.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endDateTime=endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()); 
      return kardexRepository.findMovementsInDateRange(productId, startDateTime, endDateTime)
      .stream().map(kardexEntityQueryMapper::toDomain)
      .collect(Collectors.toList());
    }

    @Override
    public List<Kardex> findMovementsByProductBeforeDate(Long productId, LocalDate startDate) {
        ZonedDateTime startDateTime=startDate.atStartOfDay(ZoneId.systemDefault());
        return kardexRepository.findMovementsBeforeDate(productId,startDateTime)
        .stream().map(kardexEntityQueryMapper::toDomain)
        .collect(Collectors.toList());
    }

    @Override
    public Optional<Kardex> findByRefFacture(Long factCode, Long productId) {
        return kardexRepository. findByFactCodeAndProduct_ProductId(factCode,productId)
        .map(kardexEntityQueryMapper::toDomain);
    }

    @Override
    public List<Kardex> findAvailablePurchasesOrderedByDate(Long idProduct) {
        List<KardexEntity> kardex=this.kardexRepository.findAvailablePurchasesOrderedByDate(idProduct);
        return  kardexEntityQueryMapper.toDomainList(kardex);
       
    }

    @Override
    public Optional<Kardex> findById(Long id) {
        return kardexRepository.findById(id)
        .map(kardexEntityQueryMapper::toDomain);
    }
    
}
