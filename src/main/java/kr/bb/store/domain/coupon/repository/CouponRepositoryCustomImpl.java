package kr.bb.store.domain.coupon.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.bb.store.domain.coupon.dto.CouponForOwnerDto;
import kr.bb.store.domain.coupon.dto.QCouponForOwnerDto;

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
        return queryFactory
                .select(new QCouponForOwnerDto(
                        coupon.store.id,
                        coupon.couponCode,
                        coupon.couponName,
                        coupon.minPrice,
                        coupon.discountPrice,
                        coupon.limitCount.subtract(
                                JPAExpressions
                                        .select(issuedCoupon.count())
                                        .from(issuedCoupon)
                                        .where(issuedCoupon.id.couponId.eq(coupon.id))
                        ),
                        coupon.startDate,
                        coupon.endDate
                ))
                .from(coupon)
                .leftJoin(issuedCoupon)
                .on(coupon.id.eq(issuedCoupon.id.couponId))
                .where(coupon.store.id.eq(storeId))
                .groupBy(
                        coupon.store.id,
                        coupon.couponCode,
                        coupon.couponName,
                        coupon.minPrice,
                        coupon.discountPrice,
                        coupon.limitCount,
                        coupon.startDate,
                        coupon.endDate,
                        coupon.id
                )
                .fetch();
    }
}
