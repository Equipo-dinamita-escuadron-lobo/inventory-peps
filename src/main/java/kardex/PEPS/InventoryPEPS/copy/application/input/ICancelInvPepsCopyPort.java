package kardex.PEPS.InventoryPEPS.copy.application.input;

import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyCancelResponseDto;

/**
 * Puerto de entrada: cancelar un proceso de copia de inventory-peps.
 */
public interface ICancelInvPepsCopyPort {

    CopyCancelResponseDto cancelar(String idProceso);
}
