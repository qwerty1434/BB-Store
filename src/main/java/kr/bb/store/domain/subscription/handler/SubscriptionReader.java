package kr.bb.store.domain.subscription.handler;

import kr.bb.store.domain.subscription.entity.Subscription;
import kr.bb.store.domain.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionReader {
    private final SubscriptionRepository subscriptionRepository;

    public List<Subscription> readByStoreId(Long storeId) {
        return subscriptionRepository.findAllByStoreId(storeId);
    }
}
