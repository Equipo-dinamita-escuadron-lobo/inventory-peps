package kardex.PEPS.InventoryPEPS.application.ports.input;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.KardexMigration;
import kardex.PEPS.InventoryPEPS.domain.model.KardexReport;

public interface IKardexQueryPort {
    Page<KardexReport> getRecordsKardexByProduct(Long productId, LocalDate start, LocalDate end,Pageable pageable);
    List<Kardex> getKardexAvailableQuantityByProduct(Long productId);

    /**
     * @brief Gets the last kardex record for all products of an enterprise
     * @param enterpriseId Enterprise identifier to filter products
     * @return List of latest kardex records for each product
     */
    List<KardexMigration> findLastKardexForAllProducts(String enterpriseId);

}
