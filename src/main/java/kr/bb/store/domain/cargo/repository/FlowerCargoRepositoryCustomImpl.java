package kr.bb.store.domain.cargo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

public class FlowerCargoRepositoryCustomImpl implements FlowerCargoRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public FlowerCargoRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
