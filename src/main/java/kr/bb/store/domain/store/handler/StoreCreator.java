package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.dto.DeliveryPolicyDto;
import kr.bb.store.domain.store.dto.StoreAddressDto;
import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.exception.CannotOwnMultipleStoreException;
import kr.bb.store.domain.store.repository.DeliveryPolicyRepository;
import kr.bb.store.domain.store.repository.StoreAddressRepository;
import kr.bb.store.domain.store.repository.StoreRepository;
import kr.bb.store.domain.store.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class StoreCreator {
    private final StoreRepository storeRepository;
    private final DeliveryPolicyRepository deliveryPolicyRepository;
    private final StoreAddressRepository storeAddressRepository;

    public Store create(Long userId, StoreCreateRequest storeCreateRequest, Sido sido, Gugun gugun) {
        Store store = createStore(userId, storeCreateRequest.toStoreRequest());
        createDeliveryPolicy(store,storeCreateRequest.toDeliveryPolicyRequest());
        createStoreAddress(sido,gugun,store,storeCreateRequest.toStoreAddressRequest());
        return store;
    }

    public Store createStore(Long userId, StoreDto storeDto) {
        if(ownerAlreadyHavingStore(userId)) {
            throw new CannotOwnMultipleStoreException();
        }

        Store store = Store.builder()
                .storeManagerId(userId)
                .storeCode(UUID.randomUUID().toString().substring(0,8))
                .storeName(storeDto.getStoreName())
                .detailInfo(storeDto.getDetailInfo())
                .storeThumbnailImage(storeDto.getStoreThumbnailImage())
                .phoneNumber(storeDto.getPhoneNumber())
                .accountNumber(storeDto.getAccountNumber())
                .bank(storeDto.getBank())
                .build();
        return storeRepository.save(store);
    }

    private DeliveryPolicy createDeliveryPolicy(Store store, DeliveryPolicyDto deliveryPolicyDto) {
        DeliveryPolicy deliveryPolicy = DeliveryPolicy.builder()
                .store(store)
                .freeDeliveryMinPrice(deliveryPolicyDto.getFreeDeliveryMinPrice())
                .deliveryPrice(deliveryPolicyDto.getDeliveryPrice())
                .build();
        return deliveryPolicyRepository.save(deliveryPolicy);
    }

    private StoreAddress createStoreAddress(Sido sido, Gugun gugun, Store store, StoreAddressDto storeAddressDto) {
        StoreAddress storeAddress = StoreAddress.builder()
                .store(store)
                .sido(sido)
                .gugun(gugun)
                .address(storeAddressDto.getAddress())
                .detailAddress(storeAddressDto.getDetailAddress())
                .zipCode(storeAddressDto.getZipCode())
                .lat(storeAddressDto.getLat())
                .lon(storeAddressDto.getLon())
                .build();
        return storeAddressRepository.save(storeAddress);
    }


    private boolean ownerAlreadyHavingStore(Long userId) {
        return storeRepository.findByStoreManagerId(userId).isPresent();
    }
}


