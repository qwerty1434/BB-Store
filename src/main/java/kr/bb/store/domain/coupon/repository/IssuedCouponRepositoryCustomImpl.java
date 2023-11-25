package kr.bb.store.domain.coupon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static kr.bb.store.domain.coupon.entity.QIssuedCoupon.issuedCoupon;

public class IssuedCouponRepositoryCustomImpl implements IssuedCouponRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public IssuedCouponRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Long findIssuedCountByCouponId(Long couponId) {
        return queryFactory.select(issuedCoupon.count())
                .from(issuedCoupon)
                .where(issuedCoupon.id.couponId.eq(couponId))
                .fetchOne();
    }
}
