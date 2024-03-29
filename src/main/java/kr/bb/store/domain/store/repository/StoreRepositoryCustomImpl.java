package kr.bb.store.domain.store.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.bb.store.domain.store.controller.response.QStoreForMapResponse;
import kr.bb.store.domain.store.controller.response.QStoreListResponse;
import kr.bb.store.domain.store.controller.response.StoreForMapResponse;
import kr.bb.store.domain.store.controller.response.StoreListResponse;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.address.Gugun;
import kr.bb.store.domain.store.entity.address.Sido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static kr.bb.store.domain.store.entity.QStore.store;
import static kr.bb.store.domain.store.entity.QStoreAddress.storeAddress;

public class StoreRepositoryCustomImpl implements StoreRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public StoreRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<StoreListResponse> getStoresWithPaging(Pageable pageable) {
        List<StoreListResponse> contents = queryFactory.select(new QStoreListResponse(
                    store.id,
                    store.storeThumbnailImage,
                    store.storeName,
                    store.detailInfo,
                    store.averageRating,
                    Expressions.asBoolean(false),
                    storeAddress.address,
                    storeAddress.detailAddress
                ))
                .from(store)
                .leftJoin(storeAddress)
                .on(store.id.eq(storeAddress.id))
                .where(
                        store.isDeleted.isFalse()
                )
                .orderBy(store.averageRating.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();
        Long count = queryFactory
                .select(store.id.count())
                .from(store)
                .where(
                        store.isDeleted.isFalse()
                )
                .fetchOne();
        return new PageImpl<>(contents,pageable,count);
    }

    @Override
    public List<StoreForMapResponse> getNearbyStores(double centerLat, double centerLon, double meter) {
        return queryFactory.select(new QStoreForMapResponse(
                    store.id,
                    store.storeName,
                    store.detailInfo,
                    store.storeThumbnailImage,
                    store.averageRating,
                    storeAddress.lat,
                    storeAddress.lon,
                    storeAddress.address,
                    storeAddress.detailAddress
                ))
                .from(storeAddress)
                .leftJoin(storeAddress.store, store)
                .where(
                        withinRadius(centerLat, centerLon, meter),
                        store.isDeleted.isFalse()
                )
                .orderBy(store.averageRating.desc())
                .fetch();
    }

    @Override
    public List<StoreForMapResponse> getStoresWithRegion(Sido sido, Gugun gugun) {
        return queryFactory.select(new QStoreForMapResponse(
                        store.id,
                        store.storeName,
                        store.detailInfo,
                        store.storeThumbnailImage,
                        store.averageRating,
                        storeAddress.lat,
                        storeAddress.lon,
                        storeAddress.address,
                        storeAddress.detailAddress
                ))
                .from(storeAddress)
                .leftJoin(storeAddress.store, store)
                .where(
                        storeAddress.sido.eq(sido),
                        gugun != null ? storeAddress.gugun.eq(gugun) : null,
                        store.isDeleted.isFalse()
                )
                .orderBy(store.averageRating.desc())
                .fetch();
    }

    @Override
    public Page<Store> getStoresWithRegionAndPagingOrderByCreatedAt(Pageable pageable, Sido sido, Gugun gugun) {
        List<Store> contents = queryFactory.select(store)
                .from(storeAddress)
                .leftJoin(storeAddress.store, store)
                .where(
                        storeAddress.sido.eq(sido),
                        gugun != null ? storeAddress.gugun.eq(gugun) : null,
                        store.isDeleted.isFalse()
                )
                .orderBy(store.createdAt.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long count = queryFactory
                .select(store.id.count())
                .from(storeAddress)
                .leftJoin(storeAddress.store, store)
                .where(
                        storeAddress.sido.eq(sido),
                        gugun != null ? storeAddress.gugun.eq(gugun) : null,
                        store.isDeleted.isFalse()
                )
                .fetchOne();
        return new PageImpl<>(contents,pageable,count);
    }

    @Override
    public Page<Store> getStoresWithRegionAndPagingOrderByAverageRating(Pageable pageable, Sido sido, Gugun gugun) {
        List<Store> contents = queryFactory.select(store)
                .from(storeAddress)
                .leftJoin(storeAddress.store, store)
                .where(
                        storeAddress.sido.eq(sido),
                        gugun != null ? storeAddress.gugun.eq(gugun) : null,
                        store.isDeleted.isFalse()
                )
                .orderBy(store.averageRating.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long count = queryFactory
                .select(store.id.count())
                .from(storeAddress)
                .leftJoin(storeAddress.store, store)
                .where(
                        storeAddress.sido.eq(sido),
                        gugun != null ? storeAddress.gugun.eq(gugun) : null,
                        store.isDeleted.isFalse()
                )
                .fetchOne();
        return new PageImpl<>(contents,pageable,count);
    }

    @Override
    public Page<Store> getStoresWithReginAndPagingOrderByMonthlySalesRevenue(Pageable pageable, Sido sido, Gugun gugun) {
        List<Store> contents = queryFactory.select(store)
                .from(storeAddress)
                .leftJoin(storeAddress.store, store)
                .where(
                        storeAddress.sido.eq(sido),
                        gugun != null ? storeAddress.gugun.eq(gugun) : null,
                        store.isDeleted.isFalse()
                )
                .orderBy(store.monthlySalesRevenue.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long count = queryFactory
                .select(store.id.count())
                .from(storeAddress)
                .leftJoin(storeAddress.store, store)
                .where(
                        storeAddress.sido.eq(sido),
                        gugun != null ? storeAddress.gugun.eq(gugun) : null,
                        store.isDeleted.isFalse()
                )
                .fetchOne();
        return new PageImpl<>(contents,pageable,count);
    }

    private OrderSpecifier<Double> nearbyStoreOrderer(double centerLat, double centerLon) {
        return storeAddress.lat.abs().subtract(centerLat)
                .add(storeAddress.lon.abs().subtract(centerLon))
                .asc();
    }

    private BooleanExpression withinRadius(double centerLat, double centerLon, double meter) {
        return storeAddress.lat.between(centerLat - metersToLatitude(meter), centerLat + metersToLatitude(meter))
                .and(storeAddress.lon.between(centerLon - metersToLongitude(centerLat, meter), centerLon + metersToLongitude(centerLat, meter)));
    }

    private static double metersToLatitude(double meters) {
        // 위도 1도당 거리 계산 (Haversine 공식 사용)
        double latDiff = meters / 6371000.0; // 지구 반지름: 6371km (미터 단위로 변환)
        return Math.toDegrees(latDiff);
    }

    private static double metersToLongitude(double centerLat, double meters) {
        // 경도 1도당 거리 계산 (Haversine 공식 사용)
        double latRadians = Math.toRadians(centerLat);
        double lonDiff = meters / (6371000.0 * Math.cos(latRadians)); // 지구 반지름: 6371km (미터 단위로 변환)
        return Math.toDegrees(lonDiff);
    }

}
