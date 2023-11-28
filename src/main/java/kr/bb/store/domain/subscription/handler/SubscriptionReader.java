package kr.bb.store.domain.subscription.handler;

import kr.bb.store.domain.subscription.entity.Subscription;
import kr.bb.store.domain.subscription.exception.SubscriptionNotFoundException;
import kr.bb.store.domain.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionReader {
    private final SubscriptionRepository subscriptionRepository;

    public Subscription readByOrderSubscriptionId(Long orderSubscriptionId) {
        return subscriptionRepository.findByOrderSubscriptionId(orderSubscriptionId)
                .orElseThrow(SubscriptionNotFoundException::new);
    }
}
