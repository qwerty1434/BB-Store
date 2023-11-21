package kr.bb.store.domain.cargo.repository;

import kr.bb.store.domain.cargo.entity.FlowerCargo;
import kr.bb.store.domain.cargo.entity.FlowerCargoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlowerCargoRepository extends JpaRepository<FlowerCargo, FlowerCargoId>,FlowerCargoRepositoryCustom {
}
