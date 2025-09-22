package kardex.PEPS.InventoryPEPS.domain.port.output;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;

public interface IKardexQueryOutputPort {
   List<Kardex> getAllKardex(Long idProduct);

   int getAvailableAmountByProduct(Long idProduct);

   Optional<Kardex> getFirstRecordByAmountAvailable(Long idProduct);

   List<Kardex> findMovementsByProductAndDateRange(Long productId,LocalDate startDate,LocalDate endDate);
    List<Kardex> findMovementsByProductBeforeDate(Long productId, LocalDate startDate);
   
    Optional<Kardex> findByRefFacture(Long factCode, Long productId);

    List<Kardex> findAvailablePurchasesOrderedByDate(Long idProduct);

    Optional<Kardex> findById(Long id);


}
