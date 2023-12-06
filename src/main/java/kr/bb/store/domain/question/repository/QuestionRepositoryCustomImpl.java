package kr.bb.store.domain.question.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.bb.store.domain.question.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

import java.util.List;

import static kr.bb.store.domain.question.entity.QAnswer.answer;
import static kr.bb.store.domain.question.entity.QQuestion.question;

public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public QuestionRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<QuestionForOwnerDto> getQuestionsForStoreOwnerWithPaging(Long storeId, Boolean isReplied, Pageable pageable) {
        List<QuestionForOwnerDto> contents = queryFactory.select(new QQuestionForOwnerDto(
                        question.id,
                        question.productName,
                        question.nickname,
                        question.title,
                        question.createdAt,
                        Expressions.asBoolean(answer.question.id.isNotNull()),
                        question.isRead
                ))
                .from(answer)
                .rightJoin(answer.question, question)
                .where(
                        isReplied != null ? checkRepliedCondition(isReplied) : null,
                        question.isDeleted.isFalse()
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long count = queryFactory
                .select(question.id.count())
                .from(answer)
                .rightJoin(answer.question, question)
                .where(
                        isReplied != null ? checkRepliedCondition(isReplied) : null,
                        question.isDeleted.isFalse()
                )
                .fetchOne();
        return new PageImpl<>(contents,pageable,count);

    }

    @Override
    public Page<QuestionInProductDto> getQuestionsInProductWithPaging(Long userId, Long productId, Boolean isReplied, Pageable pageable) {
        List<QuestionInProductDto> contents = queryFactory.select(new QQuestionInProductDto(
                        question.id,
                        Expressions.asBoolean(answer.question.id.isNotNull()),
                        question.title,
                        question.content,
                        question.nickname,
                        question.createdAt,
                        question.isSecret,
                        question.userId.eq(userId),
                        answer.content,
                        answer.createdAt
                ))
                .from(answer)
                .rightJoin(answer.question, question)
                .where(
                        isReplied != null ? checkRepliedCondition(isReplied) : null,
                        question.productId.eq(productId),
                        question.isDeleted.isFalse()
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long count = queryFactory
                .select(question.id.count())
                .from(answer)
                .rightJoin(answer.question, question)
                .where(
                        isReplied != null ? checkRepliedCondition(isReplied) : null,
                        question.productId.eq(productId),
                        question.isDeleted.isFalse()
                )
                .fetchOne();
        return new PageImpl<>(contents,pageable,count);

    }

    @Override
    public Page<MyQuestionInMypageDto> getMyQuestionsInProductWithPaging(Long userId, Long productId, Boolean isReplied, Pageable pageable) {
        List<MyQuestionInMypageDto> contents = queryFactory.select(new QMyQuestionInMypageDto(
                        question.id,
                        Expressions.asBoolean(answer.question.id.isNotNull()),
                        question.title,
                        question.content,
                        question.nickname,
                        question.createdAt,
                        answer.content,
                        answer.createdAt
                ))
                .from(answer)
                .rightJoin(answer.question, question)
                .where(
                        isReplied != null ? checkRepliedCondition(isReplied) : null,
                        question.productId.eq(productId),
                        question.userId.eq(userId),
                        question.isDeleted.isFalse()
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long count = queryFactory
                .select(question.id.count())
                .from(answer)
                .rightJoin(answer.question, question)
                .where(
                        isReplied != null ? checkRepliedCondition(isReplied) : null,
                        question.productId.eq(productId),
                        question.userId.eq(userId),
                        question.isDeleted.isFalse()
                )
                .fetchOne();
        return new PageImpl<>(contents,pageable,count);

    }

    @Override
    public Page<MyQuestionInMypageDto> getMyQuestionsWithPaging(Long userId, Boolean isReplied, Pageable pageable) {
        List<MyQuestionInMypageDto> contents = queryFactory.select(new QMyQuestionInMypageDto(
                        question.id,
                        Expressions.asBoolean(answer.question.id.isNotNull()),
                        question.title,
                        question.content,
                        question.nickname,
                        question.createdAt,
                        answer.content,
                        answer.createdAt
                ))
                .from(answer)
                .rightJoin(answer.question, question)
                .where(
                        isReplied != null ? checkRepliedCondition(isReplied) : null,
                        question.userId.eq(userId),
                        question.isDeleted.isFalse()
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long count = queryFactory
                .select(question.id.count())
                .from(answer)
                .rightJoin(answer.question, question)
                .where(
                        isReplied != null ? checkRepliedCondition(isReplied) : null,
                        question.userId.eq(userId),
                        question.isDeleted.isFalse()
                )
                .fetchOne();
        return new PageImpl<>(contents,pageable,count);

    }

    private BooleanExpression checkRepliedCondition(boolean isReplied) {
        return isReplied ? answer.question.id.isNotNull() :
                answer.question.id.isNull();

    }
}
