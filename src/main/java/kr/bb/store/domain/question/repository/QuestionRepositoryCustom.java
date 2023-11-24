package kr.bb.store.domain.question.repository;

import kr.bb.store.domain.question.dto.MyQuestionInMypageDto;
import kr.bb.store.domain.question.dto.MyQuestionInProductDto;
import kr.bb.store.domain.question.dto.QuestionForOwnerDto;
import kr.bb.store.domain.question.dto.QuestionInProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepositoryCustom {
    Page<QuestionForOwnerDto> getQuestionsForStoreOwnerWithPaging(Long storeId, Boolean isReplied, Pageable pageable);
    Page<QuestionInProductDto> getQuestionsInProductWithPaging(Long userId, Long productId, Boolean isReplied, Pageable pageable);
    Page<MyQuestionInProductDto> getMyQuestionsInProductWithPaging(Long userId, Long productId, Boolean isReplied, Pageable pageable);
    Page<MyQuestionInMypageDto> getMyQuestionsWithPaging(Long userId, Boolean isReplied, Pageable pageable);
}
