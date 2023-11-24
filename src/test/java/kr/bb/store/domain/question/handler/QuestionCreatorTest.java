package kr.bb.store.domain.question.handler;

import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.entity.Question;
import kr.bb.store.domain.question.repository.QuestionRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class QuestionCreatorTest {
    @Autowired
    private QuestionCreator questionCreator;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private EntityManager em;

    @DisplayName("질문 정보를 전달받아 질문을 생성한다")
    @Test
    void createQuestion() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Long customerId = 2L;

        QuestionCreateRequest questionCreateRequest = createQuestionCreateRequest(store.getId());

        // when
        Question question = questionCreator.create(customerId, questionCreateRequest);

        // then
        assertThat(question.getId()).isNotNull();
        assertThat(question.getStore()).isNotNull();
        assertThat(question.getTitle()).isEqualTo("질문제목");

    }

    @DisplayName("질문이 처음 생성됐을 때 확인 여부는 항상 '읽지 않음'이다")
    @Test
    void isReadAlwaysFalseWhenQuestionCreated() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Long customerId = 2L;

        QuestionCreateRequest questionCreateRequest = createQuestionCreateRequest(store.getId());

        // when
        Question question = questionCreator.create(customerId, questionCreateRequest);

        em.flush();
        em.clear();

        Question savedQuestion = questionRepository.findById(question.getId()).get();

        // then
        assertThat(savedQuestion.getIsRead()).isFalse();
    }



    private QuestionCreateRequest createQuestionCreateRequest(Long storeId) {
        return QuestionCreateRequest.builder()
                .storeId(storeId)
                .productId(1L)
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