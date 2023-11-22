package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.exception.DeliveryPolicyNotFoundException;
import kr.bb.store.domain.store.exception.StoreAddressNotFoundException;
import kr.bb.store.domain.store.exception.StoreNotFoundException;
import kr.bb.store.domain.store.handler.response.DetailInfoResponse;
import kr.bb.store.domain.store.handler.response.StoreInfoUserResponse;
import kr.bb.store.domain.store.repository.DeliveryPolicyRepository;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class StoreReader {
    private final StoreRepository storeRepository;
    private final StoreAddressRepository storeAddressRepository;
    private final DeliveryPolicyRepository deliveryPolicyRepository;

    public DetailInfoResponse readDetailInfo(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findByStoreId(storeId)
                .orElseThrow(DeliveryPolicyNotFoundException::new);
        StoreAddress storeAddress = storeAddressRepository.findByStoreId(storeId)
                .orElseThrow(StoreAddressNotFoundException::new);

        return DetailInfoResponse.of(store,deliveryPolicy,storeAddress);
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
                .averageRating(store.getAverageRating())
                .detailInfo(store.getDetailInfo())
                .phoneNumber(store.getPhoneNumber())
                .isLiked(isLiked)
                .isSubscribed(isSubscribed)
                .build();
    }
}
