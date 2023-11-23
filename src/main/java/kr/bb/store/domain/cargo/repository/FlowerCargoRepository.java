package kr.bb.store.domain.cargo.repository;

import kr.bb.store.domain.cargo.entity.FlowerCargo;
import kr.bb.store.domain.cargo.entity.FlowerCargoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlowerCargoRepository extends JpaRepository<FlowerCargo, FlowerCargoId>,FlowerCargoRepositoryCustom {
    List<FlowerCargo> findAllByStoreId(Long storeId);
}
