package kr.bb.store.domain.question.handler;

import kr.bb.store.domain.question.dto.*;
import kr.bb.store.domain.question.controller.response.QuestionDetailInfoResponse;
import kr.bb.store.domain.question.entity.Answer;
import kr.bb.store.domain.question.entity.Question;
import kr.bb.store.domain.question.exception.QuestionNotFoundException;
import kr.bb.store.domain.question.repository.AnswerRepository;
import kr.bb.store.domain.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class QuestionReader {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionDetailInfoResponse readDetailInfo(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(QuestionNotFoundException::new);
        question.check();

        Answer answer = answerRepository.findById(questionId)
                .orElse(null);

        return QuestionDetailInfoResponse.of(question,answer);
    }

    public Page<QuestionForOwnerDto> readQuestionsForStoreOwner(Long storeId, Boolean isReplied, Pageable pageable) {
        return questionRepository.getQuestionsForStoreOwnerWithPaging(storeId, isReplied, pageable);
    }

    public Page<QuestionInProductDto> readQuestionsInProduct(Long userId, String productId, Boolean isReplied, Pageable pageable) {
        return questionRepository.getQuestionsInProductWithPaging(userId, productId, isReplied, pageable);
    }

    public Page<MyQuestionInMypageDto> readMyQuestionsInProduct(Long userId, String productId, Boolean isReplied, Pageable pageable) {
        return questionRepository.getMyQuestionsInProductWithPaging(userId, productId, isReplied, pageable);
    }

    public Page<MyQuestionInMypageDto> readQuestionsForMypage(Long userId, Boolean isReplied, Pageable pageable) {
        return questionRepository.getMyQuestionsWithPaging(userId, isReplied, pageable);
    }

    public Question read(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(QuestionNotFoundException::new);
    }
}
