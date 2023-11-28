package kr.bb.store.domain.subscription.service;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.handler.StoreReader;
import kr.bb.store.domain.subscription.controller.request.SubscriptionCreateRequest;
import kr.bb.store.domain.subscription.entity.Subscription;
import kr.bb.store.domain.subscription.handler.SubscriptionCreator;
import kr.bb.store.domain.subscription.handler.SubscriptionManager;
import kr.bb.store.domain.subscription.handler.SubscriptionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionCreator subscriptionCreator;
    private final SubscriptionReader subscriptionReader;
    private final SubscriptionManager subscriptionManager;
    private final StoreReader storeReader;

    @Transactional
    public Subscription createSubscription(SubscriptionCreateRequest subscriptionCreateRequest) {
        Store store = storeReader.findStoreById(subscriptionCreateRequest.getStoreId());
        return subscriptionCreator.create(store, subscriptionCreateRequest);
    }

    @Transactional
    public void softDeleteSubscription(Long orderSubscriptionId) {
        Subscription subscription = subscriptionReader.readByOrderSubscriptionId(orderSubscriptionId);
        subscriptionManager.softDelete(subscription);
    }

}
