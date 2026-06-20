package kardex.PEPS.InventoryPEPS.application.service.command;

import org.springframework.stereotype.Service;

import kardex.PEPS.InventoryPEPS.application.ports.input.IDetailOutputCommandPort;
import kardex.PEPS.InventoryPEPS.domain.port.output.command.IDetailOutputCommandOutPutPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetailOutputCommandService implements IDetailOutputCommandPort {
    private final IDetailOutputCommandOutPutPort detailOutputCommandOutPutPort;

    @Override
    public void deleteAll() {
        detailOutputCommandOutPutPort.deleteAll();
    }

}
