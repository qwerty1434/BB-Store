package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.exception.CannotOwnMultipleStoreException;
import kr.bb.store.domain.store.repository.StoreRepository;
import kr.bb.store.domain.store.handler.request.StoreRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class StoreCreator {
    private final StoreRepository storeRepository;

    public Store create(Long userId, StoreRequest storeRequest) {
        if(ownerAlreadyHavingStore(userId)) {
            throw new CannotOwnMultipleStoreException();
        }

        Store store = Store.builder()
                .storeManagerId(userId)
                .storeCode(UUID.randomUUID().toString())
                .storeName(storeRequest.getStoreName())
                .detailInfo(storeRequest.getDetailInfo())
                .storeThumbnailImage(storeRequest.getStoreThumbnailImage())
                .phoneNumber(storeRequest.getPhoneNumber())
                .accountNumber(storeRequest.getAccountNumber())
                .bank(storeRequest.getBank())
                .build();
        return storeRepository.save(store);
    }

    private boolean ownerAlreadyHavingStore(Long userId) {
        return storeRepository.findByStoreManagerId(userId).isPresent();
    }
}


