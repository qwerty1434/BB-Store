package kr.bb.store.domain.store.service;

import kr.bb.store.domain.cargo.dto.FlowerDto;
import kr.bb.store.domain.cargo.service.CargoService;
import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.controller.response.*;
import kr.bb.store.domain.store.dto.GugunDto;
import kr.bb.store.domain.store.dto.SidoDto;
import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.handler.*;
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
    private final StoreReader storeReader;
    private final SidoReader sidoReader;
    private final GugunReader gugunReader;
    private final CargoService cargoService;


    @Transactional
    public Long createStore(Long userId, StoreCreateRequest storeCreateRequest, List<FlowerDto> flowers) {
        Sido sido = sidoReader.readSidoByName(storeCreateRequest.getSido());
        Gugun gugun = gugunReader.readGugunCorrespondingSido(sido, storeCreateRequest.getGugun());
        Store store = storeCreator.create(userId, storeCreateRequest, sido, gugun);
        cargoService.createBasicCargo(store, flowers);
        return store.getId();
    }

    @Transactional
    public void editStoreInfo(Long storeId, StoreInfoEditRequest storeInfoEditRequest) {
        Store store = storeReader.findStoreById(storeId);
        StoreAddress storeAddress = storeReader.findStoreAddressByStoreId(storeId);
        DeliveryPolicy deliveryPolicy = storeReader.findDeliveryPolicyByStoreId(storeId);
        Sido sido = sidoReader.readSidoByName(storeInfoEditRequest.getSido());
        Gugun gugun = gugunReader.readGugunCorrespondingSido(sido, storeInfoEditRequest.getGugun());
        storeManager.edit(store, storeAddress, deliveryPolicy, sido, gugun, storeInfoEditRequest);
    }

    public StoreDetailInfoResponse getStoreInfo(Long storeId) {
        return storeReader.readDetailInfo(storeId);
    }

    public SimpleStorePagingResponse getStoresWithPaging(Pageable pageable) {
        Page<StoreListResponse> storePages = storeReader.readStoresWithPaging(pageable);

        // TODO : 좋아요 여부 feign으로 받아와서 채우기
        List<StoreListResponse> contents = storePages.getContent().stream()
                .collect(Collectors.toList());

        return SimpleStorePagingResponse.builder()
                .stores(contents)
                .totalCnt(storePages.getTotalElements())
                .build();
    }

    public StoreInfoUserResponse getStoreInfoForUser(Long storeId) {
        // TODO : Feign통신으로 값 받아오기
        Boolean isLiked = false;
        Boolean isSubscribed = false;
        String subscriptionProductId = "구독상품아이디";
        return storeReader.readForUser(storeId, isLiked, isSubscribed, subscriptionProductId);
    }

    public StoreInfoManagerResponse getStoreInfoForManager(Long storeId) {
        return storeReader.readForManager(storeId);
    }

    public StoreListForMapResponse getNearbyStores(Double lat, Double lon, Integer level) {
        // TODO : 좋아요 여부 feign으로 받아와서 채우기
        StoreListForMapResponse nearbyStores = storeReader.getNearbyStores(lat, lon, level);

        return nearbyStores;
    }
    public StoreListForMapResponse getStoresWithRegion(String sidoCode, String gugunCode) {
        // TODO : 좋아요 여부 feign으로 받아와서 채우기
        Sido sido = sidoReader.readSido(sidoCode);
        Gugun gugun = "".equals(gugunCode) ? null : gugunReader.readGugunCorrespondingSidoWithCode(sido, gugunCode);
        StoreListForMapResponse storesWithRegion = storeReader.getStoresWithRegion(sido, gugun);
        return storesWithRegion;
    }

    public Long getStoreId(Long userId) {
        return storeReader.getStoreByUserId(userId).getId();
    }

    public List<SidoDto> getSido() {
        return sidoReader.readAll()
                .stream()
                .map(SidoDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<GugunDto> getGugun(String sidoCode) {
        return gugunReader.readGuguns(sidoCode)
                .stream()
                .map(GugunDto::fromEntity)
                .collect(Collectors.toList());
    }
}
