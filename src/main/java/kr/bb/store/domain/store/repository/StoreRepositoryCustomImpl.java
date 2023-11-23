package kr.bb.store.domain.store.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.handler.response.QStoreForMapResponse;
import kr.bb.store.domain.store.handler.response.StoreForMapResponse;
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
    public Page<Store> getStoresWithPaging(Pageable pageable) {
        List<Store> contents = queryFactory.selectFrom(store)
                .where(
                        store.isDeleted.isFalse()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
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
    public List<StoreForMapResponse> getNearbyStores(double lat, double lon, double radius) {
        return queryFactory.select(new QStoreForMapResponse(
                    store.id,
                    store.storeName,
                    store.detailInfo,
                    store.averageRating,
                    storeAddress.lat,
                    storeAddress.lon
                ))
                .from(storeAddress)
                .leftJoin(storeAddress.store, store)
                .where(
                        withinRadius(lat, lon, radius)
                )
                .fetch();
    }

    private BooleanExpression withinRadius(double centerLat, double centerLon, double radius) {
        return storeAddress.lat.between(centerLat - calculateLatDifference(radius), centerLat + calculateLatDifference(radius))
                .and(storeAddress.lon.between(centerLon - calculateLonDifference(centerLat, radius), centerLon + calculateLonDifference(centerLat, radius)));
    }

    private double calculateLatDifference(double radius) {
        // 위도 1도 == 111km
        return radius / 111.0;
    }

    private double calculateLonDifference(double centerLat, double radius) {
        double latRadians = Math.toRadians(centerLat);
        // 경도 1도 == 111 * cos(위도) km
        return radius / (111.0 * Math.cos(latRadians));
    }

}
