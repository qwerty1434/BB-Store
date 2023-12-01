package kr.bb.store.domain.store.handler;

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


@RequiredArgsConstructor
@Component
public class StoreReader {
    private final StoreRepository storeRepository;
    private final StoreAddressRepository storeAddressRepository;
    private final DeliveryPolicyRepository deliveryPolicyRepository;


    public Store findStoreById(Long storeId) {
        return storeRepository.findById(storeId).orElseThrow(StoreAddressNotFoundException::new);
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

    public Page<Store> readStoresWithPaging(Pageable pageable) {
        return storeRepository.getStoresWithPaging(pageable);
    }

    public StoreInfoUserResponse readForUser(Long storeId, Boolean isLiked, Boolean isSubscribed) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        StoreAddress storeAddress = storeAddressRepository.findByStoreId(storeId)
                .orElseThrow(StoreAddressNotFoundException::new);
        return StoreInfoUserResponse.builder()
                .storeName(store.getStoreName())
                .storeThumbnailImage(store.getStoreThumbnailImage())
                .address(storeAddress.getAddress())
                .detailAddress(storeAddress.getDetailAddress())
                .averageRating(store.getAverageRating())
                .detailInfo(store.getDetailInfo())
                .phoneNumber(store.getPhoneNumber())
                .isLiked(isLiked)
                .isSubscribed(isSubscribed)
                .build();
    }

    public StoreInfoManagerResponse readForManager(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        StoreAddress storeAddress = storeAddressRepository.findByStoreId(storeId)
                .orElseThrow(StoreAddressNotFoundException::new);
        return StoreInfoManagerResponse.builder()
                .storeName(store.getStoreName())
                .storeThumbnailImage(store.getStoreThumbnailImage())
                .phoneNumber(store.getPhoneNumber())
                .accountNumber(store.getAccountNumber())
                .bank(store.getBank())
                .detailInfo(store.getDetailInfo())
                .address(storeAddress.getAddress())
                .addressDetail(storeAddress.getDetailAddress())
                .build();
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

    public Store getStoreByUserId(Long userId) {
        return storeRepository.findByStoreManagerId(userId)
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
