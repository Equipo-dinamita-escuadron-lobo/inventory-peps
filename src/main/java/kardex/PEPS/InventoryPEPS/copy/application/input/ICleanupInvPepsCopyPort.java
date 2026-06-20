package kardex.PEPS.InventoryPEPS.copy.application.input;

/**
 * Puerto de entrada: limpiar registros de log de un proceso de copia de inventory-peps.
 */
public interface ICleanupInvPepsCopyPort {

    void limpiar(String idProceso);
}
