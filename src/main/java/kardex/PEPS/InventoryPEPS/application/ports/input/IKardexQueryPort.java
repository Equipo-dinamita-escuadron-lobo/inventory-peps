package kardex.PEPS.InventoryPEPS.application.ports.input;

import java.time.LocalDate;
import java.util.List;

import kardex.PEPS.InventoryPEPS.domain.model.KardexReport;

public interface IKardexQueryPort {
    List<KardexReport> getRecordsKardexByProduct(Long productId, LocalDate start, LocalDate end);
    
}
