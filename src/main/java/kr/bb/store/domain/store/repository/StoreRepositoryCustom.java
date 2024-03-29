package kr.bb.store.domain.store.repository;

import kr.bb.store.domain.store.controller.response.StoreListResponse;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.Sido;
import kr.bb.store.domain.store.controller.response.StoreForMapResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreRepositoryCustom {
    Page<StoreListResponse> getStoresWithPaging(Pageable pageable);

    List<StoreForMapResponse> getNearbyStores(double lat, double lon, double radius);

    List<StoreForMapResponse> getStoresWithRegion(Sido sido, Gugun gugun);

    Page<Store> getStoresWithRegionAndPagingOrderByCreatedAt(Pageable pageable, Sido sido, Gugun gugun);

    Page<Store> getStoresWithRegionAndPagingOrderByAverageRating(Pageable pageable, Sido sido, Gugun gugun);

    Page<Store> getStoresWithReginAndPagingOrderByMonthlySalesRevenue(Pageable pageable, Sido sido, Gugun gugun);

}
