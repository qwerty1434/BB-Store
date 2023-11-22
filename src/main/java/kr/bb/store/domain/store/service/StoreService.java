package kr.bb.store.domain.store.service;

import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.handler.StoreCreator;
import kr.bb.store.domain.store.handler.StoreManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StoreService {
    private final StoreCreator storeCreator;
    private final StoreManager storeManager;

    @Transactional
    public void createStore(Long userId) {
        storeCreator.create(userId);
    }

    @Transactional
    public void editStoreInfo(Long storeId, StoreInfoEditRequest storeInfoEditRequest) {
        storeManager.edit(storeId, storeInfoEditRequest);
    }
}
