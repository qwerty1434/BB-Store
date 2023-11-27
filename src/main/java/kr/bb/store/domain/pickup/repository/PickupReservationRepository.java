package kr.bb.store.domain.pickup.repository;

import kr.bb.store.domain.pickup.entity.PickupReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PickupReservationRepository extends JpaRepository<PickupReservation,Long>,PickupReservationRepositoryCustom {
    List<PickupReservation> findAllByStoreId(Long storeId);
}
