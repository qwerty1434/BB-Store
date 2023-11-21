package kr.bb.store.domain.question.repository;

import kr.bb.store.domain.question.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer,Long>,AnswerRepositoryCustom {
}
