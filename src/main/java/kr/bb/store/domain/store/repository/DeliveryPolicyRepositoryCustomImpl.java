package kr.bb.store.domain.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

public class DeliveryPolicyRepositoryCustomImpl implements DeliveryPolicyRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public DeliveryPolicyRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
