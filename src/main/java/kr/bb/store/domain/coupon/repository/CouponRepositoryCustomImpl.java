package kr.bb.store.domain.coupon.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import kr.bb.store.domain.coupon.dto.QCouponForOwnerDto;
import kr.bb.store.domain.coupon.entity.QIssuedCoupon;

import javax.persistence.EntityManager;
import java.util.List;

import static kr.bb.store.domain.coupon.entity.QCoupon.coupon;
import static kr.bb.store.domain.coupon.entity.QIssuedCoupon.issuedCoupon;

public class CouponRepositoryCustomImpl implements CouponRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public CouponRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<CouponForOwnerDto> findAllDtoByStoreId(Long storeId) {
        return queryFactory.select(new QCouponForOwnerDto(
                    coupon.storeId,
                    coupon.couponCode,
                    coupon.couponName,
                    coupon.minPrice,
                    coupon.discountPrice,
                    coupon.limitCount.subtract(issuedCoupon.id.count()),
                    coupon.startDate,
                    coupon.endDate
                ))
                .from(coupon)
                .leftJoin(issuedCoupon)
                .on(coupon.id.eq(issuedCoupon.id.couponId))
                .where(coupon.storeId.eq(storeId))
                .groupBy(coupon.id)
                .fetch();
    }
}
