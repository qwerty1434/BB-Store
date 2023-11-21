package kr.bb.store.domain.subscription.repository;

import kr.bb.store.domain.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long>,SubscriptionRepositoryCustom {
}
