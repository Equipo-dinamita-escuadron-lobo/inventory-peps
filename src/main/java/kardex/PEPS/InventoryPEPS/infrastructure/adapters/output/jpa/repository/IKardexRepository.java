package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.KardexEntity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;



public interface  IKardexRepository extends JpaRepository<KardexEntity, Long>{
   List<KardexEntity> findByProductId(Long idProduct);

      @Query("SELECT SUM(k.availableQuantity) "+
         "FROM KardexEntity k "+
         "WHERE k.product.productId = :productId")
      int sumAvailableAmountByProduct(@Param("productId") Long productId);


      @Query("SELECT k "+
          "FROM KardexEntity k "+
          "WHERE k.product.productId = :productId "+
          "AND k.availableQuantity > 0 "+
          "ORDER BY k.date ASC ")
      Optional<KardexEntity> findFirstByAvailableAmount(@Param("productId") Long productId);


      @Modifying
      @Transactional
      @Query("UPDATE KardexEntity k SET k.availableQuantity = :newAmount WHERE k.idKardex = :idKardex")
      int updateAvaliableAmount(@Param("idKardex") Long idKardex, @Param("newAmount") int newAmount);


      //Obtiene los movimientos para el reporte(dentro del rango)
      @Query("SELECT k "+
      "FROM KardexEntity k "+ 
      "LEFT JOIN FETCH k.detailsOutput "+
      "WHERE k.product.productId = :productId AND k.date >= :startDate AND k.date <= :endDate ORDER BY k.date ASC")
      List<KardexEntity> findMovementsInDateRange(Long productId, ZonedDateTime startDate, ZonedDateTime endDate);
   

      //obtiene el saldo inicial antes del rango
      @Query("SELECT k "+
      "FROM KardexEntity k "+
      "LEFT JOIN FETCH k.detailsOutput "+
      "WHERE k.product.productId = :productId AND k.date < :startDate ORDER BY k.date ASC")
      List<KardexEntity> findMovementsBeforeDate(Long productId, ZonedDateTime startDate);


      //para buscar el poducto que se va devolver
      @Query("SELECT k FROM KardexEntity k WHERE k.factCode = :factCode AND k.product.id = :productId")
      Optional<KardexEntity> findByFactCode(@Param("factCode") long factCode, @Param("productId")long product);

      /**
     * Busca un registro de Kardex por su factCode y por el ID de negocio (productId) del producto asociado.
     */
     Optional<KardexEntity> findByFactCodeAndProduct_ProductId(Long factCode, Long productId);

      @Query("SELECT k FROM KardexEntity k " +
           "WHERE k.product.productId = :productId " +
           "AND k.availableQuantity > 0 " +
           "ORDER BY k.date ASC")
      List<KardexEntity> findAvailablePurchasesOrderedByDate(@Param("productId") Long productId);

     

   }
