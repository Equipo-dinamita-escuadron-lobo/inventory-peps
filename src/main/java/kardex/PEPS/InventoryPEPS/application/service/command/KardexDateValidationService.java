package kardex.PEPS.InventoryPEPS.application.service.command;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.port.output.IFormatterResultOutputPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.IMessageServicePort;
import kardex.PEPS.InventoryPEPS.domain.port.output.external.IConfigClientPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.i18n.MessageKeys;
import lombok.RequiredArgsConstructor;

/**
 * @brief Service for validating kardex dates
 * 
 * Ensures kardex dates are valid according to accounting rules
 * and assigns default dates when missing.
 */
@Service
@RequiredArgsConstructor
public class KardexDateValidationService {
    private final IConfigClientPort  configClientPort;  
    private IFormatterResultOutputPort formatterResultOutputPort;
    private final IMessageServicePort messageServicePort;


    /**
     * @brief Assigns current date if not present and validates against accounting calendar
     * @param kardex Kardex object with optional date
     * @param enterpiseId Enterprise identifier for calendar validation
     */
    public void validateAndSetDate(Kardex kardex,String enterpiseId){

        if(kardex.getDate() == null){
            kardex.addDate();
        }

        LocalDate kardexDate = kardex.getDate().toLocalDate();

        if(!configClientPort.isValidAccountingDate(enterpiseId, kardexDate)){
            String errorMessage =messageServicePort.getMessage(MessageKeys.INVALID_ACCOUNTING_DATE );
            formatterResultOutputPort.returnErrorGenericResponse(400, errorMessage);
        }
    }

}
