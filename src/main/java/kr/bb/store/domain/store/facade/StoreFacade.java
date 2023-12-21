package kr.bb.store.domain.store.facade;

import bloomingblooms.domain.flower.FlowerDto;
import bloomingblooms.domain.store.StoreInfoDto;
import bloomingblooms.domain.store.StoreNameAndAddressDto;
import kr.bb.store.client.ProductFeignClient;
import kr.bb.store.client.StoreLikeFeignClient;
import kr.bb.store.client.StoreSubscriptionFeignClient;
import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.controller.response.*;
import kr.bb.store.domain.store.dto.GugunDto;
import kr.bb.store.domain.store.dto.SidoDto;
import kr.bb.store.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StoreFacade {
    private final StoreService storeService;
    private final ProductFeignClient productFeignClient;
    private final StoreLikeFeignClient storeLikeFeignClient;
    private final StoreSubscriptionFeignClient storeSubscriptionFeignClient;


    public Long createStore(Long userId, StoreCreateRequest storeCreateRequest) {
        List<FlowerDto> flowers = productFeignClient.getFlowers().getData();
        return storeService.createStore(userId, storeCreateRequest, flowers);
    }

    public void editStoreInfo(Long storeId, StoreInfoEditRequest storeInfoEditRequest) {
        storeService.editStoreInfo(storeId, storeInfoEditRequest);
    }

    public SimpleStorePagingResponse getStoresWithLikes(Long userId, Pageable pageable) {
        Page<StoreListResponse> storePages = storeService.getStoresWithPaging(pageable);
        List<Long> storeIds = storePages.getContent()
                .stream()
                .map(StoreListResponse::getStoreId)
                .collect(Collectors.toList());

        if(isNotGuest(userId)) {
            Map<Long, Boolean> storeLikes = storeLikeFeignClient.getStoreLikes(userId, storeIds).getData();
            storePages.getContent().forEach(store -> store.setIsLiked(storeLikes.get(store.getStoreId())));
        }

        return SimpleStorePagingResponse.builder()
                .stores(storePages.getContent())
                .totalCnt(storePages.getTotalElements())
                .build();
    }

    public StoreInfoUserResponse getStoreInfoForUser(Long userId, Long storeId) {
        String subscriptionProductId = productFeignClient.getSubscriptionProductId(storeId).getData();
        if(isNotGuest(userId)) {
            Map<Long, Boolean> storeLikes = storeLikeFeignClient.getStoreLikes(userId, List.of(storeId)).getData();
            Map<Long, Boolean> storeSubscriptions = storeSubscriptionFeignClient
                    .getStoreSubscriptions(userId, List.of(storeId)).getData();
            Boolean isLiked = storeLikes.get(storeId);
            Boolean isSubscribed = storeSubscriptions.get(storeId);
            return storeService.getStoreInfoForUser(storeId, isLiked, isSubscribed, subscriptionProductId);
        }
        return storeService.getStoreInfoForUser(storeId, false, false, subscriptionProductId);

    }

    public StoreInfoManagerResponse getStoreInfoForManager(Long storeId) {
        return storeService.getStoreInfoForManager(storeId);
    }

    public StoreListForMapResponse getNearbyStores(Long userId, Double lat, Double lon, Integer level) {
        StoreListForMapResponse nearbyStores = storeService.getNearbyStores(lat, lon, level);

        if(isNotGuest(userId)) {
            List<Long> storeIds = nearbyStores.getStoreIds();
            Map<Long, Boolean> storeLikes = storeLikeFeignClient.getStoreLikes(userId, storeIds).getData();
            nearbyStores.setLikes(storeLikes);
        }

        return nearbyStores;
    }

    public StoreListForMapResponse getStoresWithRegion(Long userId, String sidoCode, String gugunCode) {
        StoreListForMapResponse storesWithRegion = storeService.getStoresWithRegion(sidoCode, gugunCode);

        if(isNotGuest(userId)) {
            List<Long> storeIds = storesWithRegion.getStoreIds();
            Map<Long, Boolean> storeLikes = storeLikeFeignClient.getStoreLikes(userId, storeIds).getData();
            storesWithRegion.setLikes(storeLikes);
        }

        return storesWithRegion;
    }

    public Long getStoreId(Long userId) {
        return storeService.getStoreId(userId);
    }

    public String getStoreName(Long storeId) {
        return storeService.getStoreName(storeId);
    }

    public StoreNameAndAddressDto getStoreNameAndAddress(Long storeId) {
        return storeService.getStoreNameAndAddress(storeId).toCommonEntity();
    }

    public StoreDetailInfoResponse getStoreDetailInfo(Long storeId) {
        return storeService.getStoreDetailInfo(storeId);
    }

    public StoreInfoDto getStoreInfo(Long userId) {
        return storeService.getStoreInfo(userId).toCommonEntity();
    }

    public List<StoreInfoDto> getAllStoreInfos() {
        return storeService.getAllStoreInfos().stream()
                .map(kr.bb.store.client.dto.StoreInfoDto::toCommonEntity)
                .collect(Collectors.toList());
    }

    public List<SidoDto> getSido() {
        return storeService.getSido();
    }

    public List<GugunDto> getGugun(String sidoCode) {
        return storeService.getGugun(sidoCode);
    }

    private boolean isNotGuest(Long userId) {
        return userId != null;
    }

}