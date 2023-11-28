package kr.bb.store.domain.subscription.handler;

import kr.bb.store.domain.subscription.entity.Subscription;
import kr.bb.store.domain.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionManager {
    private final SubscriptionRepository subscriptionRepository;

    public void softDelete(Subscription subscription) {
        subscription.softDelete();
    }
}
