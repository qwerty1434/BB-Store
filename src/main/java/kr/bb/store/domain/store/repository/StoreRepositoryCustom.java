package kr.bb.store.domain.store.repository;

import kr.bb.store.domain.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreRepositoryCustom {
    Page<Store> getStoresWithPaging(Pageable pageable);
}
