package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.adapter;

import org.springframework.stereotype.Component;

import kardex.PEPS.InventoryPEPS.domain.port.output.command.IDetailOutputCommandOutPutPort;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository.IDetailOutPutRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DetailOutputCommandAdapter implements IDetailOutputCommandOutPutPort {

    private final IDetailOutPutRepository detailOutPutRepository;
    /**
     * @brief Deletes all detail output records from the database
     */
    @Override
    public void deleteAll() {
        detailOutPutRepository.deleteAll();
    }
    
}
