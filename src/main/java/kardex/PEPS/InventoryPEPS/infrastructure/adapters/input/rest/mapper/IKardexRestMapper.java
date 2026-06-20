package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.KardexMigration;
import kardex.PEPS.InventoryPEPS.domain.model.KardexReport;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.model.SaleDetail;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexAvailableQuantityDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.AdjustmentEntryDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.AdjustmentExitDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.AdjustmentEntryDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexRecordsDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.AdjustmentExitDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.ListLastProductKardexDtoResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.SaleDetailDTO;

@Mapper(componentModel ="spring")
public interface IKardexRestMapper {
    
    @Mapping(target = "product", source = "productId", qualifiedByName = "idToProduct")
   @Mapping(target = "date", ignore = true)
    @Mapping(target = "idKardex", ignore = true)
    @Mapping(target = "type", ignore = true)
    Kardex toDomain(AdjustmentEntryDTORequest adjustmentEntryDTORequest);

    @Mapping(target = "product", source = "productId", qualifiedByName = "idToProduct")
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "idKardex", ignore = true)
    @Mapping(target = "type", ignore = true)
    Kardex toDomain(AdjustmentExitDTORequest AdjustmentDTORequest);


    AdjustmentEntryDTOResponse toDTOResponse(Kardex kardex);

    @Named("idToProduct")
    default Product mapIdToProduct(Long productId) {
        if (productId == null) {
            return null;
        }
        Product product = new Product();
        product.setProductId(productId); // Asignamos el ID al campo 'id' del producto
        return product;
    }

 
    AdjustmentExitDTOResponse toDTOResponseSale(Kardex Kardex);

    List<KardexRecordsDTOResponse> toDTORenponseRecords(List<KardexReport> kardex);

    List<AdjustmentEntryDTOResponse> toDTOSaleReturn(List<Kardex> sales);
    List<KardexAvailableQuantityDTOResponse> toKardexDTO(List<Kardex> listKardex);

    KardexRecordsDTOResponse toDTORecord(KardexReport kardexReport);
    
    // Mapea los detalles de salida
    SaleDetailDTO toSaleDetailDTO(SaleDetail saleDetail);
    
    // Mapea listas de detalles
    List<SaleDetailDTO> toSaleDetailDTOs(List<SaleDetail> saleDetails);

    List<ListLastProductKardexDtoResponse> toListLastProductKardexDtoResponseList(List<KardexMigration> kardexList);

    
}
