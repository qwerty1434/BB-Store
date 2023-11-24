package kr.bb.store.domain.question.service;

import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.controller.response.QuestionDetailInfoResponse;
import kr.bb.store.domain.question.controller.response.QuestionsForOwnerPagingResponse;
import kr.bb.store.domain.question.dto.QuestionForOwnerDto;
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

    public QuestionDetailInfoResponse getQuestionInfo(Long questionId) {
        // TODO : Feign통신으로 값 받아오기
        String nickname = "유저명";
        String productName = "제품명";
        return questionReader.readDetailInfo(questionId, nickname, productName);
    }

    @Transactional
    public Answer createAnswer(Long questionId, String content) {
        return answerCreator.create(questionId, content);
    }

    public QuestionsForOwnerPagingResponse getQuestionsForStoreOwner(Long storeId, Boolean isReplied, Pageable pageable) {
        Page<QuestionForOwnerDto> questionForOwnerDtos = questionReader.readQuestionsForStoreOwner(storeId, isReplied, pageable);
        return QuestionsForOwnerPagingResponse.builder()
                .data(questionForOwnerDtos.getContent())
                .totalCnt(questionForOwnerDtos.getTotalElements())
                .build();
    }
}
