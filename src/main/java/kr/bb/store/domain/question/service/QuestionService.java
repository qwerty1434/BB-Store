package kr.bb.store.domain.question.service;

import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.handler.QuestionCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class QuestionService {
    private final QuestionCreator questionCreator;

    @Transactional
    public void createQuestion(Long userId, QuestionCreateRequest questionCreateRequest) {
        questionCreator.create(userId, questionCreateRequest);
    }


}
