package kr.bb.store.domain.store.service;

import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.GugunRepository;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.entity.address.SidoRepository;
import kr.bb.store.domain.store.exception.address.GugunNotFoundException;
import kr.bb.store.domain.store.exception.address.InvalidParentException;
import kr.bb.store.domain.store.exception.address.SidoNotFoundException;
import kr.bb.store.domain.store.handler.*;
import kr.bb.store.domain.store.handler.response.*;
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
    private final SidoReader sidoReader;
    private final GugunReader gugunReader;


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
                .totalCnt(storePages.getTotalElements())
                .build();
    }

    public StoreInfoUserResponse getStoreInfoForUser(Long storeId) {
        // TODO : Feign통신으로 값 받아오기
        Boolean isLiked = false;
        Boolean isSubscribed = false;
        return storeReader.readForUser(storeId, isLiked, isSubscribed);
    }

    public StoreInfoManagerResponse getStoreInfoForManager(Long storeId) {
        return storeReader.readForManager(storeId);
    }

    public StoreListForMapResponse getNearbyStores(Double lat, Double lon) {
        // TODO : 좋아요 여부 feign으로 받아와서 채우기
        StoreListForMapResponse nearbyStores = storeReader.getNearbyStores(lat, lon);

        return nearbyStores;
    }
    public StoreListForMapResponse getStoresWithRegion(String sidoName, String gugunName) {
        // TODO : 좋아요 여부 feign으로 받아와서 채우기
        Sido sido = sidoReader.readSido(sidoName);
        Gugun gugun = "".equals(gugunName) ? null : gugunReader.readGugun(sido, gugunName);
        StoreListForMapResponse storesWithRegion = storeReader.getStoresWithRegion(sido, gugun);
        return storesWithRegion;
    }
}
