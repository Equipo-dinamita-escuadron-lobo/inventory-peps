package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.jpa.entity.DetailOutputEntity;

public interface  IDetailOutPutRepository extends JpaRepository<DetailOutputEntity,Long>{
    
}
