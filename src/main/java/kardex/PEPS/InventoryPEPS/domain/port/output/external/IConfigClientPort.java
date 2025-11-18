package kardex.PEPS.InventoryPEPS.domain.port.output.external;

import java.time.LocalDate;

public interface IConfigClientPort {
    boolean isValidAccountingDate(String enterpriseId, LocalDate date);
}
