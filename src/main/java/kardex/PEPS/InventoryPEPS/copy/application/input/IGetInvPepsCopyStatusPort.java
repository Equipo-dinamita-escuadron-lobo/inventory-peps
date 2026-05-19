package kardex.PEPS.InventoryPEPS.copy.application.input;

import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyStatusResponseDto;

/**
 * Puerto de entrada: consultar el estado de un proceso de copia de inventory-peps.
 */
public interface IGetInvPepsCopyStatusPort {

    CopyStatusResponseDto obtenerEstado(String idProceso);
}
