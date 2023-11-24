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
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public Answer create(Long questionId, String content) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(QuestionNotFoundException::new);

        Answer answer = Answer.builder()
                .question(question)
                .content(content)
                .build();

        return answerRepository.save(answer);
    }
}
