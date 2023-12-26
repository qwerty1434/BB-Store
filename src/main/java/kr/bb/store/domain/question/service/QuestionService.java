package kr.bb.store.domain.question.service;

import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.controller.response.*;
import kr.bb.store.domain.question.dto.MyQuestionInMypageDto;
import kr.bb.store.domain.question.dto.QuestionForOwnerDto;
import kr.bb.store.domain.question.dto.QuestionInProductDto;
import kr.bb.store.domain.question.entity.Answer;
import kr.bb.store.domain.question.entity.Question;
import kr.bb.store.domain.question.handler.AnswerCreator;
import kr.bb.store.domain.question.handler.QuestionCreator;
import kr.bb.store.domain.question.handler.QuestionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class QuestionService {
    private final QuestionCreator questionCreator;
    private final AnswerCreator answerCreator;
    private final QuestionReader questionReader;

    @Transactional
    public Question createQuestion(Long userId, QuestionCreateRequest questionCreateRequest) {
        return questionCreator.create(userId, questionCreateRequest);
    }

    @Transactional
    public QuestionDetailInfoResponse getQuestionInfo(Long questionId) {
        return questionReader.readDetailInfo(questionId);
    }

    @Transactional
    public Answer createAnswer(Long questionId, String content) {
        return answerCreator.create(questionId, content);
    }

    public QuestionsForOwnerPagingResponse getQuestionsForStoreOwner(Long storeId, Boolean isReplied, Pageable pageable) {
        Page<QuestionForOwnerDto> questionForOwnerDtos = questionReader.readQuestionsForStoreOwner(storeId, isReplied, pageable);
        return QuestionsForOwnerPagingResponse.from(questionForOwnerDtos);
    }

    public QuestionsInProductPagingResponse getQuestionsInProduct(Long userId, String productId, Boolean isReplied, Pageable pageable) {
        Page<QuestionInProductDto> questionInProductDtos = questionReader.readQuestionsInProduct(userId, productId, isReplied, pageable);
        return QuestionsInProductPagingResponse.from(questionInProductDtos);
    }

    public MyQuestionsInProductPagingResponse getMyQuestionsInProduct(Long userId, String productId, Boolean isReplied, Pageable pageable) {
        Page<MyQuestionInMypageDto> questionInProductDtos = questionReader.readMyQuestionsInProduct(userId, productId, isReplied, pageable);
        return MyQuestionsInProductPagingResponse.from(questionInProductDtos);
    }

    public MyQuestionsInMypagePagingResponse getMyQuestions(Long userId, Boolean isReplied, Pageable pageable) {
        Page<MyQuestionInMypageDto> myQuestionInMypageDtos = questionReader.readQuestionsForMypage(userId, isReplied, pageable);
        return MyQuestionsInMypagePagingResponse.builder()
                .data(myQuestionInMypageDtos.getContent())
                .totalCnt(myQuestionInMypageDtos.getTotalElements())
                .build();
    }

    public Question getQuestionById(Long questionId) {
        return questionReader.read(questionId);
    }
}
