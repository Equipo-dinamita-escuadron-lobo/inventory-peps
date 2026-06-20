package kardex.PEPS.InventoryPEPS.copy.domain.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un proceso de copia de inventory-peps.
 * ADR-38.
 */
public class DuplicateCopyJobException extends RuntimeException {

    public DuplicateCopyJobException(String idProceso, int fase) {
        super(String.format("No se encontró proceso de copia invpeps con idProceso=%s fase=%d",
                idProceso, fase));
    }
}
