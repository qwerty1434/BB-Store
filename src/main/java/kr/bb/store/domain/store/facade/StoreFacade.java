package kr.bb.store.domain.store.facade;

import bloomingblooms.domain.flower.FlowerDto;
import bloomingblooms.domain.notification.order.OrderType;
import bloomingblooms.domain.order.ValidatePolicyDto;
import bloomingblooms.domain.store.StoreAverageDto;
import bloomingblooms.domain.store.StoreInfoDto;
import bloomingblooms.domain.store.StoreNameAndAddressDto;
import bloomingblooms.domain.store.StorePolicy;
import bloomingblooms.domain.wishlist.likes.LikedStoreInfoResponse;
import bloomingblooms.dto.command.UpdateSettlementCommand;
import bloomingblooms.dto.response.SettlementStoreInfoResponse;
import kr.bb.store.client.ProductFeignClient;
import kr.bb.store.client.StoreLikeFeignClient;
import kr.bb.store.client.StoreSubscriptionFeignClient;
import kr.bb.store.domain.coupon.service.CouponService;
import kr.bb.store.domain.store.controller.request.SortType;
import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.controller.response.*;
import kr.bb.store.domain.store.dto.DeliveryPolicyDto;
import kr.bb.store.domain.store.dto.GugunDto;
import kr.bb.store.domain.store.dto.SidoDto;
import kr.bb.store.domain.store.dto.StoreForAdminDto;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreFacade {
    private final StoreService storeService;
    private final CouponService couponService;
    private final ProductFeignClient productFeignClient;
    private final StoreLikeFeignClient storeLikeFeignClient;
    private final StoreSubscriptionFeignClient storeSubscriptionFeignClient;

    @KafkaListener(topics = "store-average-rating-update", groupId = "average-rating")
    @CacheEvict(cacheNames = "store-list-with-paging", allEntries = true)
    public void updateAverageRating(StoreAverageDto storeAverageDto) {
        storeService.updateAverageRating(storeAverageDto.getAverage());
        log.info("stores averageRating updated");
    }

    @KafkaListener(topics = "settlement", groupId = "settlement")
    @CacheEvict(cacheNames = "store-list-with-paging", allEntries = true)
    public void updateMonthlySalesRevenue(UpdateSettlementCommand updateSettlementCommand) {
        storeService.updateMonthlySalesRevenue(updateSettlementCommand.getDtoList());
        log.info("stores monthlySalesRevenue updated");
    }

    @CacheEvict(cacheNames = "store-list-with-paging", allEntries = true)
    public Long createStore(Long userId, StoreCreateRequest storeCreateRequest) {
        List<FlowerDto> flowers = productFeignClient.getFlowers().getData();
        return storeService.createStore(userId, storeCreateRequest, flowers);
    }

    @CacheEvict(cacheNames = "store-list-with-paging", allEntries = true)
    public void editStoreInfo(Long storeId, StoreInfoEditRequest storeInfoEditRequest) {
        storeService.editStoreInfo(storeId, storeInfoEditRequest);
        log.info("info of store {} edited", storeId);
    }

    public SimpleStorePagingResponse getStoresWithLikes(Long userId, Pageable pageable) {
        Page<StoreListResponse> storePages = storeService.getStoresWithPaging(pageable);
        List<Long> storeIds = storePages.getContent()
                .stream()
                .map(StoreListResponse::getStoreId)
                .collect(Collectors.toList());

        if(isLoginUser(userId)) {
            Map<Long, Boolean> storeLikes = storeLikeFeignClient.getStoreLikes(userId, storeIds).getData();
            storePages.getContent().forEach(store -> store.setIsLiked(storeLikes.get(store.getStoreId())));
        }

        return SimpleStorePagingResponse.builder()
                .stores(storePages.getContent())
                .totalCnt(storePages.getTotalElements())
                .build();
    }

    public StoreInfoUserResponse getStoreInfoForUser(Long userId, Long storeId) {
        String subscriptionProductId = productFeignClient.getSubscriptionProductId(storeId).getData()
                .getSubscriptionProductId();

        if(isLoginUser(userId)) {
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

        if(isLoginUser(userId)) {
            List<Long> storeIds = nearbyStores.getStoreIds();
            Map<Long, Boolean> storeLikes = storeLikeFeignClient.getStoreLikes(userId, storeIds).getData();
            nearbyStores.setLikes(storeLikes);
        }

        return nearbyStores;
    }

    public StoreListForMapResponse getStoresWithRegion(Long userId, String sidoCode, String gugunCode) {
        StoreListForMapResponse storesWithRegion = storeService.getStoresWithRegion(sidoCode, gugunCode);

        if(isLoginUser(userId)) {
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

    public Map<Long, String> getStoreNames(List<Long> storeIds) {
        return storeService.getStoreNames(storeIds);
    }

    public StoreNameAndAddressDto getStoreNameAndAddress(Long storeId) {
        return storeService.getStoreNameAndAddress(storeId).toCommonDto();
    }

    public StoreDetailInfoResponse getStoreDetailInfo(Long storeId) {
        return storeService.getStoreDetailInfo(storeId);
    }

    public StoreInfoDto getStoreInfo(Long userId) {
        return storeService.getStoreInfo(userId).toCommonDto();
    }

    public List<StoreInfoDto> getAllStoreInfos() {
        return storeService.getAllStoreInfos().stream()
                .map(kr.bb.store.client.dto.StoreInfoDto::toCommonDto)
                .collect(Collectors.toList());
    }

    public void validateForOrder(ValidatePolicyDto validatePolicyDto) {
        couponService.validateCouponPrice(validatePolicyDto.getValidatePriceDtos());
        if(!validatePolicyDto.getOrderType().equals(OrderType.PICKUP)) {
            storeService.validateDeliveryPrice(validatePolicyDto.getValidatePriceDtos());
        }
    }

    public List<LikedStoreInfoResponse> simpleInfos(List<Long> storeIds){
        return storeService.simpleInfos(storeIds).stream()
                .map(kr.bb.store.domain.store.controller.response.LikedStoreInfoResponse::toCommonDto)
                .collect(Collectors.toList());
    }

    public List<SettlementStoreInfoResponse> storeInfoForSettlement(List<Long> storeIds) {
        return storeService.storeInfoForSettlement(storeIds);
    }

    public DeliveryPolicyDto getDeliveryPolicy(Long storeId) {
        return storeService.getDeliveryPolicy(storeId);
    }

    public Map<Long, StorePolicy> getDeliveryPolicies(List<Long> storeIds) {
        return storeService.getDeliveryPolicies(storeIds);
    }

    public StoreForAdminDtoResponse getStoresForAdmin(Pageable pageable, SortType sort, String sidoCode, String gugunCode) {
        Page<Store> storesForAdmin = storeService.getStoresForAdmin(pageable, sort, sidoCode, gugunCode);
        List<StoreForAdminDto> data = storesForAdmin.getContent()
                .stream()
                .map(StoreForAdminDto::fromEntity)
                .collect(Collectors.toList());

        return StoreForAdminDtoResponse.of(data, storesForAdmin.getTotalElements());
    }

    public List<SidoDto> getAllSido() {
        return storeService.getAllSido();
    }

    public List<GugunDto> getGuguns(String sidoCode) {
        return storeService.getGuguns(sidoCode);
    }

    private boolean isLoginUser(Long userId) {
        return userId != null;
    }

}
