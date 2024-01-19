package kr.bb.store.domain.question.service;

import kr.bb.store.domain.BasicIntegrationTestEnv;
import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.controller.response.QuestionDetailInfoResponse;
import kr.bb.store.domain.question.entity.Answer;
import kr.bb.store.domain.question.entity.Question;
import kr.bb.store.domain.question.repository.AnswerRepository;
import kr.bb.store.domain.question.repository.QuestionRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionServiceTest extends BasicIntegrationTestEnv {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private StoreRepository storeRepository;


    @DisplayName("질문 Id를 바탕으로 질문 상세정보를 받아온다")
    @Test
    void readDetailInfo() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question question = createQuestion(store);
        questionRepository.save(question);

        Answer answer = createAnswer(question);
        answerRepository.save(answer);

        // when
        QuestionDetailInfoResponse questionDetailInfoResponse = questionService.getQuestionInfo(question.getId());

        // then
        assertThat(questionDetailInfoResponse.getTitle()).isEqualTo("질문제목");
        assertThat(questionDetailInfoResponse.getAnswer().getContent()).isEqualTo("답변내용");

    }

    @DisplayName("질문 정보를 전달받아 질문을 생성한다")
    @Test
    void createQuestion() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Long customerId = 2L;

        QuestionCreateRequest questionCreateRequest = createQuestionCreateRequest(store.getId());

        // when
        Question question = questionService.createQuestion(customerId, questionCreateRequest);

        // then
        assertThat(question.getId()).isNotNull();
        assertThat(question.getStore()).isNotNull();
        assertThat(question.getTitle()).isEqualTo("질문제목");

    }

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
        Answer answer = questionService.createAnswer(question,content);
        answerRepository.save(answer);

        // then
        assertThat(answer.getQuestion()).isNotNull();
        assertThat(answer.getContent()).isEqualTo(content);

    }



    private QuestionCreateRequest createQuestionCreateRequest(Long storeId) {
        return QuestionCreateRequest.builder()
                .storeId(storeId)
                .productId("1")
                .productName("상품명")
                .title("질문제목")
                .content("질문내용")
                .isSecret(true)
                .nickname("닉네임")
                .build();
    }

    private Question createQuestion(Store store) {
        return Question.builder()
                .store(store)
                .userId(1L)
                .productName("상품명")
                .productId("1")
                .title("질문제목")
                .content("질문내용")
                .isSecret(true)
                .nickname("닉네임")
                .build();
    }

    private Answer createAnswer(Question question) {
        return Answer.builder()
                .question(question)
                .content("답변내용")
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