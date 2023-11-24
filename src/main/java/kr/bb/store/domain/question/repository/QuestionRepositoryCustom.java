package kr.bb.store.domain.question.repository;

import kr.bb.store.domain.question.dto.QuestionForOwnerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepositoryCustom {
    Page<QuestionForOwnerDto> getQuestionsForStoreOwnerWithPaging(Long storeId, Boolean isReplied, Pageable pageable);
}
