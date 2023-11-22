package kr.bb.store.domain.store.service;

import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.handler.*;
import kr.bb.store.domain.store.handler.response.DetailInfoResponse;
import kr.bb.store.domain.store.handler.response.SimpleStorePagingResponse;
import kr.bb.store.domain.store.handler.response.SimpleStoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    public SimpleStorePagingResponse getStoresWithPaging(Pageable pageable) {
        Page<Store> storePages = storeReader.readStoresWithPaging(pageable);
        List<SimpleStoreResponse> contents = storePages.getContent().stream()
                .map(SimpleStoreResponse::from)
                .collect(Collectors.toList());

        return SimpleStorePagingResponse.builder()
                .simpleStores(contents)
                .totalCnt(storePages.getTotalPages())
                .build();
    }
}
