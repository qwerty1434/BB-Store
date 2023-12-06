package kr.bb.store.domain.question.handler;

import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.entity.Question;
import kr.bb.store.domain.question.repository.QuestionRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.exception.StoreNotFoundException;
import kr.bb.store.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class QuestionCreator {
    private final QuestionRepository questionRepository;
    private final StoreRepository storeRepository;

    public Question create(Long userId, QuestionCreateRequest questionCreateRequest) {
        Store store = storeRepository.findById(questionCreateRequest.getStoreId())
                .orElseThrow(StoreNotFoundException::new);

        Question question = Question.builder()
                .store(store)
                .userId(userId)
                .nickname(questionCreateRequest.getNickname())
                .productId(questionCreateRequest.getProductId())
                .productName(questionCreateRequest.getProductName())
                .title(questionCreateRequest.getTitle())
                .content(questionCreateRequest.getContent())
                .isSecret(questionCreateRequest.isSecret())
                .build();

        return questionRepository.save(question);
    }

}
