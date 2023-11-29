package kr.bb.store.domain.subscription.handler;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.subscription.controller.request.SubscriptionCreateRequest;
import kr.bb.store.domain.subscription.entity.Subscription;
import kr.bb.store.domain.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionCreator {
    private final SubscriptionRepository subscriptionRepository;

    public Subscription create(Store store, SubscriptionCreateRequest subscriptionCreateRequest) {

        Subscription subscription = Subscription.builder()
                .store(store)
                .orderSubscriptionId(subscriptionCreateRequest.getOrderSubscriptionId())
                .userId(subscriptionCreateRequest.getUserId())
                .subscriptionProductId(subscriptionCreateRequest.getSubscriptionProductId())
                .subscriptionCode(UUID.randomUUID().toString().substring(0,8))
                .deliveryDate(subscriptionCreateRequest.getDeliveryDate())
                .build();

        return subscriptionRepository.save(subscription);
    }
}
