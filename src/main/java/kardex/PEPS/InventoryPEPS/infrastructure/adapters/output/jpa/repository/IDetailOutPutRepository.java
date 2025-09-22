package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;

public interface  IDetailOutPutRepository extends JpaRepository<DetailOutputEntity,Long>{

  @Query("SELECT d FROM DetailOutputEntity d JOIN FETCH d.movementOrigin WHERE d.movementSale.idKardex = :idMovementSale")
  List<DetailOutputEntity> findByMovementSaleId(@Param("idMovementSale") Long idMovementSale);

}
