package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import kardex.PEPS.InventoryPEPS.domain.model.Kardex;
import kardex.PEPS.InventoryPEPS.domain.model.KardexReport;
import kardex.PEPS.InventoryPEPS.domain.model.Product;
import kardex.PEPS.InventoryPEPS.domain.model.SaleDetail;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexPurchaseDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexPurchaseReturnDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexSaleDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.request.KardexSaleReturnDTORequest;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexPurchaseDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexRecordsDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.KardexSaleDTOResponse;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response.SaleDetailDTO;

@Mapper(componentModel ="spring")
public interface IKardexRestMapper {

    @Mapping(target = "product", source = "productId", qualifiedByName = "idToProduct")
   @Mapping(target = "date", ignore = true)
    @Mapping(target = "idKardex", ignore = true)
    @Mapping(target = "type", ignore = true)
    Kardex toDomain(KardexPurchaseDTORequest kardexPurchaseDTORequest);

    @Mapping(target = "product", source = "productId", qualifiedByName = "idToProduct")
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "idKardex", ignore = true)
    @Mapping(target = "type", ignore = true)
    Kardex toDomain(KardexSaleDTORequest kardexSaleDTORequest);

    @Mapping(target = "product", source = "productId", qualifiedByName = "idToProduct")
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "type", ignore = true)
    Kardex toDomain(KardexPurchaseReturnDTORequest kardexPurchaseReturnDTORequest);

    KardexPurchaseDTOResponse toDTOResponse(Kardex kardex);

    @Named("idToProduct")
    default Product mapIdToProduct(Long productId) {
        if (productId == null) {
            return null;
        }
        Product product = new Product();
        product.setProductId(productId); // Asignamos el ID al campo 'id' del producto
        return product;
    }

  
    @Mapping(target = "product", source = "productId", qualifiedByName = "idToProduct")
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "type", ignore = true)
    Kardex toDomain(KardexSaleReturnDTORequest kardexSaleReturnDTORequest);

    KardexSaleDTOResponse toDTOResponseSale(Kardex Kardex);

    List<KardexRecordsDTOResponse> toDTORenponseRecords(List<KardexReport> kardex);

    List<KardexPurchaseDTOResponse> toDTOSaleReturn(List<Kardex> sales);

    KardexRecordsDTOResponse toDTORecord(KardexReport kardexReport);
    
    // Mapea los detalles de salida
    SaleDetailDTO toSaleDetailDTO(SaleDetail saleDetail);
    
    // Mapea listas de detalles
    List<SaleDetailDTO> toSaleDetailDTOs(List<SaleDetail> saleDetails);
    
}
