package kr.bb.store.domain.store.service;

import bloomingblooms.domain.flower.FlowerDto;
import kr.bb.store.client.StoreLikeFeignClient;
import kr.bb.store.client.StoreSubscriptionFeignClient;
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final StoreLikeFeignClient storeLikeFeignClient;
    private final StoreSubscriptionFeignClient storeSubscriptionFeignClient;


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

    public SimpleStorePagingResponse getStoresWithPaging(Long userId, Pageable pageable) {
        Page<StoreListResponse> storePages = storeReader.readStoresWithPaging(pageable);
        List<Long> storeIds = storePages.getContent()
                .stream()
                .map(StoreListResponse::getStoreId)
                .collect(Collectors.toList());

        // TODO : userId가 null이면 통신하지 말기
        Map<Long, Boolean> storeLikes = storeLikeFeignClient.getStoreLikes(userId, storeIds);

        storePages.getContent()
                .forEach(store -> store.setIsLiked(storeLikes.get(store.getStoreId())));

        return SimpleStorePagingResponse.builder()
                .stores(storePages.getContent())
                .totalCnt(storePages.getTotalElements())
                .build();
    }

    public StoreInfoUserResponse getStoreInfoForUser(Long userId, Long storeId, String subscriptionProductId) {
        // TODO : userId가 null이면 통신하지 말기
        Map<Long, Boolean> storeLikes = storeLikeFeignClient.getStoreLikes(userId, List.of(storeId));
        // TODO : feign말고 내가가진 NoSQL에서 가져오기
        Map<Long, Boolean> storeSubscriptions = storeSubscriptionFeignClient.getStoreSubscriptions(userId, List.of(storeId));
        Boolean isLiked = storeLikes.get(storeId);
        Boolean isSubscribed = storeSubscriptions.get(storeId);
        return storeReader.readForUser(storeId, isLiked, isSubscribed, subscriptionProductId);
    }

    public StoreInfoManagerResponse getStoreInfoForManager(Long storeId) {
        return storeReader.readForManager(storeId);
    }

    public StoreListForMapResponse getNearbyStores(Long userId, Double lat, Double lon, Integer level) {
        StoreListForMapResponse nearbyStores = storeReader.getNearbyStores(lat, lon, level);

        List<Long> storeIds = nearbyStores.getStoreIds();
        // TODO : userId가 null이면 통신하지 말기
        Map<Long, Boolean> storeLikes = storeLikeFeignClient.getStoreLikes(userId, storeIds);

        nearbyStores.setLikes(storeLikes);

        return nearbyStores;
    }
    public StoreListForMapResponse getStoresWithRegion(Long userId, String sidoCode, String gugunCode) {
        Sido sido = sidoReader.readSido(sidoCode);
        Gugun gugun = "".equals(gugunCode) ? null : gugunReader.readGugunCorrespondingSidoWithCode(sido, gugunCode);
        StoreListForMapResponse storesWithRegion = storeReader.getStoresWithRegion(sido, gugun);

        List<Long> storeIds = storesWithRegion.getStoreIds();
        // TODO : userId가 null이면 통신하지 말기
        Map<Long, Boolean> storeLikes = storeLikeFeignClient.getStoreLikes(userId, storeIds);

        storesWithRegion.setLikes(storeLikes);

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
