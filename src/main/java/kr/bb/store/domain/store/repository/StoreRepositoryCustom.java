package kr.bb.store.domain.store.repository;

import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.handler.response.StoreForMapResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreRepositoryCustom {
    Page<Store> getStoresWithPaging(Pageable pageable);

    List<StoreForMapResponse> getNearbyStores(double lat, double lon, double radius);
}
