package kr.bb.store.domain.question.handler;

import kr.bb.store.domain.question.entity.Answer;
import kr.bb.store.domain.question.entity.Question;
import kr.bb.store.domain.question.exception.QuestionNotFoundException;
import kr.bb.store.domain.question.repository.AnswerRepository;
import kr.bb.store.domain.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AnswerCreator {
    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content) {
        Answer answer = Answer.builder()
                .question(question)
                .content(content)
                .build();

        return answerRepository.save(answer);
    }
}
