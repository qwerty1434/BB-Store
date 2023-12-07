package kr.bb.store.domain.question.handler;

import kr.bb.store.domain.question.entity.Answer;
import kr.bb.store.domain.question.entity.Question;
import kr.bb.store.domain.question.repository.AnswerRepository;
import kr.bb.store.domain.question.repository.QuestionRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AnswerCreatorTest {
    @Autowired
    private AnswerCreator answerCreator;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private StoreRepository storeRepository;


    @DisplayName("답변 정보를 전달받아 답변을 생성한다")
    @Test
    void createAnswer() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question question = createQuestion(store);
        questionRepository.save(question);

        String content = "답변글";

        // when
        Answer answer = answerCreator.create(question.getId(),content);
        answerRepository.save(answer);

        // then
        assertThat(answer.getQuestion()).isNotNull();
        assertThat(answer.getContent()).isEqualTo(content);

    }

    @DisplayName("답변은 해당 질문과 동일한 Id값을 가져야 한다")
    @Test
    void AnswerMustHaveSameIdWithQuestion() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question q1 = createQuestion(store);
        Question q2 = createQuestion(store);
        Question q3 = createQuestion(store);
        Question q4 = createQuestion(store);
        Question q5 = createQuestion(store);
        questionRepository.saveAll(List.of(q1,q2,q3,q4,q5));

        String content = "답변글";

        // when
        Answer answer = answerCreator.create(q5.getId(),content);
        answerRepository.save(answer);

        // then
        assertThat(answer.getId()).isEqualTo(q5.getId());

    }



    private Question createQuestion(Store store) {
        return Question.builder()
                .store(store)
                .userId(1L)
                .nickname("닉네임")
                .productId("1")
                .productName("상품명")
                .title("질문제목")
                .content("질문내용")
                .isSecret(true)
                .build();
    }

    private Store createStore(Long userId) {
        return Store.builder()
                .storeManagerId(userId)
                .storeCode("가게코드")
                .storeName("가게")
                .detailInfo("가게 상세정보")
                .storeThumbnailImage("가게 썸네일")
                .phoneNumber("가게 전화번호")
                .accountNumber("가게 계좌정보")
                .bank("가게 계좌 은행정보")
                .build();
    }
}