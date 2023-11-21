package kr.bb.store.domain.question.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

public class AnswerRepositoryCustomImpl implements AnswerRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public AnswerRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
