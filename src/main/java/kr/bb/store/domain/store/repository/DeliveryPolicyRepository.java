package kr.bb.store.domain.store.repository;

import kr.bb.store.domain.store.entity.DeliveryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy,Long>,DeliveryPolicyRepositoryCustom {
}
