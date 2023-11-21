package kr.bb.store.domain.question.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public QuestionRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
