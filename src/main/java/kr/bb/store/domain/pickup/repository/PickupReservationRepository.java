package kr.bb.store.domain.pickup.repository;

import kr.bb.store.domain.pickup.entity.PickupReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupReservationRepository extends JpaRepository<PickupReservation,Long>,PickupReservationRepositoryCustom {
}
