package kardex.PEPS.InventoryPEPS.application.ports.input;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kardex.PEPS.InventoryPEPS.domain.model.KardexReport;

public interface IKardexQueryPort {
    Page<KardexReport> getRecordsKardexByProduct(Long productId, LocalDate start, LocalDate end,Pageable pageable);
    
}
