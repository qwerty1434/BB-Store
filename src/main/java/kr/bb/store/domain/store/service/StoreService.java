package kr.bb.store.domain.store.service;

import kr.bb.store.domain.store.handler.StoreCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StoreService {
    private final StoreCreator storeCreator;

    @Transactional
    public void createStore(Long userId) {
        storeCreator.create(userId);
    }
    
}
