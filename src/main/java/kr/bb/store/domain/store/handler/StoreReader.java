package kr.bb.store.domain.store.handler;

import kr.bb.store.client.dto.StoreInfoDto;
import kr.bb.store.domain.store.controller.response.*;
import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.exception.DeliveryPolicyNotFoundException;
import kr.bb.store.domain.store.exception.StoreAddressNotFoundException;
import kr.bb.store.domain.store.exception.StoreNotFoundException;
import kr.bb.store.domain.store.repository.DeliveryPolicyRepository;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
public class StoreReader {
    private final StoreRepository storeRepository;
    private final StoreAddressRepository storeAddressRepository;
    private final DeliveryPolicyRepository deliveryPolicyRepository;


    public Store findStoreById(Long storeId) {
        return storeRepository.findById(storeId).orElseThrow(StoreAddressNotFoundException::new);
    }

    public List<Store> findStoresByIds(List<Long> storeIds) {
        return storeRepository.findAllById(storeIds);
    }

    public StoreAddress findStoreAddressByStoreId(Long storeId) {
        return storeAddressRepository.findByStoreId(storeId)
                .orElseThrow(StoreAddressNotFoundException::new);
    }

    public DeliveryPolicy findDeliveryPolicyByStoreId(Long storeId) {
        return deliveryPolicyRepository.findByStoreId(storeId)
                .orElseThrow(DeliveryPolicyNotFoundException::new);
    }

    public StoreDetailInfoResponse readDetailInfo(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findByStoreId(storeId)
                .orElseThrow(DeliveryPolicyNotFoundException::new);
        StoreAddress storeAddress = storeAddressRepository.findByStoreId(storeId)
                .orElseThrow(StoreAddressNotFoundException::new);

        return StoreDetailInfoResponse.of(store,deliveryPolicy,storeAddress);
    }

    public Page<StoreListResponse> readStoresWithPaging(Pageable pageable) {
        return storeRepository.getStoresWithPaging(pageable);
    }

    public StoreInfoUserResponse readForUser(Long storeId, Boolean isLiked, Boolean isSubscribed,
                                             String subscriptionProductId) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        StoreAddress storeAddress = storeAddressRepository.findByStoreId(storeId)
                .orElseThrow(StoreAddressNotFoundException::new);
        return StoreInfoUserResponse.of(store, storeAddress, isLiked, isSubscribed, subscriptionProductId);
    }

    public StoreInfoManagerResponse readForManager(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        StoreAddress storeAddress = storeAddressRepository.findByStoreId(storeId)
                .orElseThrow(StoreAddressNotFoundException::new);
        return StoreInfoManagerResponse.of(store, storeAddress);
    }

    public StoreListForMapResponse getNearbyStores(Double lat, Double lon, Integer level) {
        List<StoreForMapResponse> nearbyStores = storeRepository.getNearbyStores(lat, lon, levelToMeter(level));
        return StoreListForMapResponse.builder()
                .stores(nearbyStores)
                .build();
    }

    public StoreListForMapResponse getStoresWithRegion(Sido sido, Gugun gugun) {
        List<StoreForMapResponse> storesWithRegion = storeRepository.getStoresWithRegion(sido, gugun);
        return StoreListForMapResponse.builder()
                .stores(storesWithRegion)
                .build();
    }

    public Optional<Store> getStoreByUserId(Long userId) {
        return storeRepository.findByStoreManagerId(userId);
    }

    public Store read(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);
    }

    public StoreInfoDto readInfo(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);
        return StoreInfoDto.fromEntity(store);
    }

    public List<StoreInfoDto> readInfos() {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(StoreInfoDto::fromEntity)
                .collect(Collectors.toList());
    }

    public StoreAddress readAddress(Long storeId) {
        return storeAddressRepository.findByStoreId(storeId)
                .orElseThrow(StoreNotFoundException::new);
    }

    private Double levelToMeter(int level) {
        switch (level) {
            case 1 :
                return 150d;
            case 2 :
                return 250d;
            case 3 :
                return 500d;
            case 4 :
                return 1000d;
            case 5 :
                return 2000d;
            default:
                throw new IllegalArgumentException("정의되지 않은 레벨입니다");
        }

    }
}
