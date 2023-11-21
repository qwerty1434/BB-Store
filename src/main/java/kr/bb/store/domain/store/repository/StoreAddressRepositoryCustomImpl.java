package kr.bb.store.domain.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

public class StoreAddressRepositoryCustomImpl implements StoreAddressRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public StoreAddressRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
