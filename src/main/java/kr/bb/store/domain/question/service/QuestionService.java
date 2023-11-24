package kr.bb.store.domain.question.service;

import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.controller.response.QuestionDetailInfoResponse;
import kr.bb.store.domain.question.entity.Question;
import kr.bb.store.domain.question.handler.QuestionCreator;
import kr.bb.store.domain.question.handler.QuestionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class QuestionService {
    private final QuestionCreator questionCreator;
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

}
