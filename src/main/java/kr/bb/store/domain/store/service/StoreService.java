package kr.bb.store.domain.store.service;

import bloomingblooms.domain.flower.FlowerDto;
import bloomingblooms.domain.order.ValidatePriceDto;
import bloomingblooms.domain.store.StorePolicy;
import bloomingblooms.dto.response.SettlementStoreInfoResponse;
import kr.bb.store.client.dto.StoreInfoDto;
import kr.bb.store.client.dto.StoreNameAndAddressDto;
import kr.bb.store.domain.cargo.service.CargoService;
import kr.bb.store.domain.store.controller.request.SortType;
import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.controller.response.*;
import kr.bb.store.domain.store.dto.DeliveryPolicyDto;
import kr.bb.store.domain.store.dto.GugunDto;
import kr.bb.store.domain.store.dto.SidoDto;
import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.exception.DeliveryInconsistencyException;
import kr.bb.store.domain.store.handler.*;
import kr.bb.store.util.RestPage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Transactional
    public void updateAverageRating(Map<Long, Double> averageRatings) {
        averageRatings.forEach((storeId, averageRating) -> {
            Store store = storeReader.read(storeId);
            store.updateAverageRating(averageRating);
        });
    }

    @Transactional
    public void updateMonthlySalesRevenue(Map<Long, Long> monthlySalesRevenues) {
        monthlySalesRevenues.forEach((storeId, monthlySalesRevenue) -> {
            Store store = storeReader.read(storeId);
            store.updateMonthlySalesRevenue(monthlySalesRevenue);
        });
    }

    public StoreDetailInfoResponse getStoreDetailInfo(Long storeId) {
        return storeReader.readDetailInfo(storeId);
    }

    // 스프링의 기본 PageImpl은 기본생성자가 존재하지 않아 String으로 저장된 캐싱 데이터를 다시 객체로 변환할 수 없음
    // RestPage객체는 @JsonCreator를 사용해 기본생성자가 아닌 인자가 있는 생성자로 직렬화/역직렬화를 할 수 있게 했다
    @Cacheable(key = "#pageable.pageNumber + '::' + #pageable.pageSize", cacheNames = "store-list-with-paging")
    public RestPage<StoreListResponse> getStoresWithPaging(Pageable pageable) {
        return new RestPage<>(storeReader.readStoresWithPaging(pageable));
    }

    public StoreInfoUserResponse getStoreInfoForUser(Long storeId, Boolean isLiked, Boolean isSubscribed, String subscriptionProductId) {
        return storeReader.readForUser(storeId, isLiked, isSubscribed, subscriptionProductId);
    }

    public StoreInfoManagerResponse getStoreInfoForManager(Long storeId) {
        return storeReader.readForManager(storeId);
    }

    public StoreListForMapResponse getNearbyStores(Double lat, Double lon, Integer level) {
        return storeReader.getNearbyStores(lat, lon, level);
    }
    public StoreListForMapResponse getStoresWithRegion(String sidoCode, String gugunCode) {
        Sido sido = sidoReader.readSido(sidoCode);
        Gugun gugun = "".equals(gugunCode) ? null : gugunReader.readGugunCorrespondingSidoWithCode(sido, gugunCode);
        return storeReader.getStoresWithRegion(sido, gugun);
    }

    public Long getStoreId(Long userId) {
        Optional<Store> storeByUserId = storeReader.getStoreByUserId(userId);
        return storeByUserId.map(Store::getId).orElse(null);
    }

    public String getStoreName(Long storeId) {
        Store store = storeReader.read(storeId);
        return store.getStoreName();
    }

    public Map<Long, String> getStoreNames(List<Long> storeIds) {
        List<Store> stores = storeReader.reads(storeIds);
        return stores.stream()
                .collect(Collectors.toMap(Store::getId, Store::getStoreName));
    }

    public StoreNameAndAddressDto getStoreNameAndAddress(Long storeId) {
        Store store = storeReader.read(storeId);
        StoreAddress storeAddress = storeReader.readAddress(storeId);
        return StoreNameAndAddressDto.of(store, storeAddress);
    }

    public StoreInfoDto getStoreInfo(Long storeId) {
        return storeReader.readInfo(storeId);
    }

    public List<StoreInfoDto> getAllStoreInfos() {
        return storeReader.readInfos();
    }

    public void validateDeliveryPrice(List<ValidatePriceDto> validatePriceDtos) {
        validatePriceDtos.forEach(dto -> {
            DeliveryPolicy deliveryPolicy = storeReader.findDeliveryPolicyByStoreId(dto.getStoreId());
            Long receivedPaymentPrice = dto.getActualAmount();
            Long receivedDeliveryPrice = dto.getDeliveryCost();
            if(!deliveryPolicy.isRightDeliveryPrice(receivedPaymentPrice, receivedDeliveryPrice)) {
                throw new DeliveryInconsistencyException();
            }
        });
    }

    public List<LikedStoreInfoResponse> simpleInfos(List<Long> storeIds) {
        return storeReader.findStoresByIds(storeIds).stream()
                .map(LikedStoreInfoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SettlementStoreInfoResponse> storeInfoForSettlement(List<Long> storeIds) {
        Map<Long, Store> stores = storeReader.findStoresByIds(storeIds).stream()
                .collect(Collectors.toMap(Store::getId, store -> store));
        Map<Long, StoreAddress> storeAddresses = storeReader.findStoreAddressesByStoreIds(storeIds).stream()
                .collect(Collectors.toMap(storeAddress -> storeAddress.getStore().getId(), storeAddress -> storeAddress));

        return storeIds.stream()
                .map(id -> SettlementStoreInfoResponse.builder()
                        .storeId(id)
                        .storeName(stores.get(id).getStoreName())
                        .bankName(stores.get(id).getBank())
                        .accountNumber(stores.get(id).getAccountNumber())
                        .sido(storeAddresses.get(id).getSido().getName())
                        .gugun(storeAddresses.get(id).getGugun().getName())
                        .build()
                ).collect(Collectors.toList());

    }


    public DeliveryPolicyDto getDeliveryPolicy(Long storeId) {
        DeliveryPolicy deliveryPolicy = storeReader.findDeliveryPolicyByStoreId(storeId);
        return DeliveryPolicyDto.fromEntity(deliveryPolicy);
    }

    public Map<Long, StorePolicy> getDeliveryPolicies(List<Long> storeIds) {
        return storeIds.stream().collect(Collectors.toMap(storeId -> storeId,
                storeId -> {
                    DeliveryPolicy deliveryPolicy = storeReader.findDeliveryPolicyByStoreId(storeId);
                    return StorePolicy.builder()
                            .storeName(deliveryPolicy.getStore().getStoreName())
                            .deliveryCost(deliveryPolicy.getDeliveryPrice())
                            .freeDeliveryMinCost(deliveryPolicy.getFreeDeliveryMinPrice())
                            .build();
                }
        ));
    }

    public Page<Store> getStoresForAdmin(Pageable pageable, SortType sort, String sidoCode, String gugunCode) {
        Sido sido = sidoReader.readSido(sidoCode);
        Gugun gugun = "".equals(gugunCode) ? null : gugunReader.readGugunCorrespondingSidoWithCode(sido, gugunCode);
        sort = (sort == null) ? SortType.DATE : sort;

        switch (sort) {
            case RATE:
                return storeReader.readStoresOrderByAverageRating(pageable, sido, gugun);
            case AMOUNT:
                return storeReader.readStoresOrderByMonthlySalesRevenue(pageable, sido, gugun);
            default:
                return storeReader.readStoresOrderByCreatedAt(pageable, sido, gugun);
        }
    }


    public List<SidoDto> getAllSido() {
        return sidoReader.readAll()
                .stream()
                .map(SidoDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<GugunDto> getGuguns(String sidoCode) {
        return gugunReader.readGuguns(sidoCode)
                .stream()
                .map(GugunDto::fromEntity)
                .collect(Collectors.toList());
    }

}
