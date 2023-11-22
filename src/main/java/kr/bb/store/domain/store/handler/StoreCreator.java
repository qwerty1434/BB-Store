package kr.bb.store.domain.store.handler;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class StoreCreator {
    private final StoreRepository storeRepository;

    public void create(Long userId) {
        Store store = Store.builder()
                .storeManagerId(userId)
                .storeCode(UUID.randomUUID().toString())
                .build();
        storeRepository.save(store);
    }
}


