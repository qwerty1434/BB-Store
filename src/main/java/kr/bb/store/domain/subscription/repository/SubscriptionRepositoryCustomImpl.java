package kr.bb.store.domain.subscription.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

public class SubscriptionRepositoryCustomImpl implements SubscriptionRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public SubscriptionRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
