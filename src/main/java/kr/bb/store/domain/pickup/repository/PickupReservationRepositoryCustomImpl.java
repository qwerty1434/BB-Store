package kr.bb.store.domain.pickup.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

public class PickupReservationRepositoryCustomImpl implements PickupReservationRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public PickupReservationRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
