package kr.bb.store.domain.store.service;

import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.handler.DeliveryPolicyCreator;
import kr.bb.store.domain.store.handler.StoreAddressCreator;
import kr.bb.store.domain.store.handler.StoreCreator;
import kr.bb.store.domain.store.handler.StoreManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StoreService {
    private final StoreCreator storeCreator;
    private final StoreManager storeManager;
    private final StoreAddressCreator storeAddressCreator;
    private final DeliveryPolicyCreator deliveryPolicyCreator;

    @Transactional
    public Long createStore(Long userId, StoreCreateRequest storeCreateRequest) {
        Store store = storeCreator.create(userId, storeCreateRequest.toStoreRequest());
        storeAddressCreator.create(store, storeCreateRequest.toStoreAddressRequest());
        deliveryPolicyCreator.create(store, storeCreateRequest.toDeliveryPolicyRequest());

        return store.getId();
    }

    @Transactional
    public void editStoreInfo(Long storeId, StoreInfoEditRequest storeInfoEditRequest) {
        storeManager.edit(storeId, storeInfoEditRequest);
    }
}
