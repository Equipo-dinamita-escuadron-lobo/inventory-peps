package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.ProductEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface IProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByProductId(Long productId);

    // Return a list of product IDs
    @Query("SELECT p.productId FROM ProductEntity p WHERE p.productId IN :ids")
    List<Long> findProductsIdByProductIdIn(@Param("ids") List<Long> ids);

    // Find all products by ID
    List<ProductEntity> findByProductIdIn(List<Long> list);


    // Find all products by enterprise ID
    Collection<ProductEntity> findAllByEnterpriseId(String enterpriseId);

    
}
