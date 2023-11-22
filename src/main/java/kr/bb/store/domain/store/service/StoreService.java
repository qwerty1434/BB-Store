package kr.bb.store.domain.store.service;

import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.handler.*;
import kr.bb.store.domain.store.handler.response.DetailInfoResponse;
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
    private final StoreReader storeReader;

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

    public DetailInfoResponse getStoreInfo(Long storeId) {
        return storeReader.readDetailInfo(storeId);
    }
}
