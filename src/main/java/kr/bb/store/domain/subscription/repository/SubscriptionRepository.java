package kr.bb.store.domain.subscription.repository;

import kr.bb.store.domain.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long>,SubscriptionRepositoryCustom {
    List<Subscription> findAllByStoreId(Long storeId);
    Optional<Subscription> findByOrderSubscriptionId(Long orderSubscriptionId);
}
