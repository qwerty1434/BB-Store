package kr.bb.store.domain.store.repository;

import kr.bb.store.domain.store.entity.DeliveryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy,Long> {
    Optional<DeliveryPolicy> findByStoreId(Long storeId);
}
