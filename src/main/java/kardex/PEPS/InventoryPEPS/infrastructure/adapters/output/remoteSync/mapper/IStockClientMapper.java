package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.mapper;

import org.mapstruct.Mapper;

import kardex.PEPS.InventoryPEPS.domain.model.Stock;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockBuyDtoRequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockDtoResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.remoteSync.dto.StockSellDtoRequest;

@Mapper(componentModel = "spring")
public interface IStockClientMapper {
    StockBuyDtoRequest toDtoRequest(Stock stock);

    Stock toDomain(StockDtoResponse dto);

    StockSellDtoRequest toSellDtoRequest(Stock stock);
}
