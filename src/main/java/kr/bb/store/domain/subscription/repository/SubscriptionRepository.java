package kr.bb.store.domain.subscription.repository;

import kr.bb.store.domain.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long>,SubscriptionRepositoryCustom {
    List<Subscription> findAllByStoreIdAndDeliveryDate(Long storeId, LocalDate date);
    Optional<Subscription> findByOrderSubscriptionId(Long orderSubscriptionId);
    List<Subscription> findAllByUserId(Long userId);
}
