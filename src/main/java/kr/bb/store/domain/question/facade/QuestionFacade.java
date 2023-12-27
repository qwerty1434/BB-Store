package kr.bb.store.domain.question.facade;

import kr.bb.store.client.UserFeignClient;
import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.controller.response.*;
import kr.bb.store.domain.question.entity.Question;
import kr.bb.store.domain.question.service.QuestionService;
import kr.bb.store.exception.NonPropagatingException;
import kr.bb.store.message.AnswerSQSPublisher;
import kr.bb.store.message.QuestionSQSPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionFacade {
    private final QuestionService questionService;
    private final QuestionSQSPublisher questionSQSPublisher;
    private final AnswerSQSPublisher answerSQSPublisher;
    private final UserFeignClient userFeignClient;

    public void createQuestion(Long userId, QuestionCreateRequest questionCreateRequest) {
        questionService.createQuestion(userId, questionCreateRequest);
        Long storeId = questionCreateRequest.getStoreId();
        questionSQSPublisher.publish(storeId);
    }

    public QuestionDetailInfoResponse getQuestionInfo(Long questionId) {
        return questionService.getQuestionInfo(questionId);
    }

    public void createAnswer(Long questionId, String content) {
        Question question = questionService.getQuestionById(questionId);
        questionService.createAnswer(question, content);
        Long userId = question.getUserId();
        try{
            String phoneNumber = userFeignClient.getPhoneNumber(userId).getData();
            answerSQSPublisher.publish(userId, phoneNumber);
        }catch (Exception e) {
            log.error(e.toString());
            log.warn("{}'s Request of '{}' failed. sqs message may not work properly", "UserFeignClient", "getPhoneNumber");
            throw new NonPropagatingException(e.getMessage());
        }
    }

    public QuestionsForOwnerPagingResponse getQuestionsForStoreOwner(Long storeId, Boolean isReplied, Pageable pageable) {
        return questionService.getQuestionsForStoreOwner(storeId, isReplied, pageable);
    }

    public QuestionsInProductPagingResponse getQuestionsInProduct(Long userId, String productId, Boolean isReplied, Pageable pageable) {
        return questionService.getQuestionsInProduct(userId, productId, isReplied, pageable);
    }

    public MyQuestionsInProductPagingResponse getMyQuestionsInProduct(Long userId, String productId, Boolean isReplied, Pageable pageable) {
        return questionService.getMyQuestionsInProduct(userId, productId, isReplied, pageable);
    }

    public MyQuestionsInMypagePagingResponse getMyQuestions(Long userId, Boolean isReplied, Pageable pageable) {
        return questionService.getMyQuestions(userId, isReplied, pageable);
    }

}
