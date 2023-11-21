package kr.bb.store.domain.coupon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

public class IssuedCouponRepositoryCustomImpl implements IssuedCouponRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public IssuedCouponRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
