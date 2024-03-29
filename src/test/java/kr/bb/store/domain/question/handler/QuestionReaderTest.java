package kr.bb.store.domain.question.handler;

import kr.bb.store.domain.BasicIntegrationTestEnv;
import kr.bb.store.domain.question.controller.response.QuestionDetailInfoResponse;
import kr.bb.store.domain.question.dto.MyQuestionInMypageDto;
import kr.bb.store.domain.question.dto.QuestionForOwnerDto;
import kr.bb.store.domain.question.dto.QuestionInProductDto;
import kr.bb.store.domain.question.entity.Answer;
import kr.bb.store.domain.question.entity.Question;
import kr.bb.store.domain.question.repository.AnswerRepository;
import kr.bb.store.domain.question.repository.QuestionRepository;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionReaderTest extends BasicIntegrationTestEnv {
    @Autowired
    private QuestionReader questionReader;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private EntityManager em;


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

        String nickname = "유저명";
        String productName = "가게명";

        // when
        QuestionDetailInfoResponse questionDetailInfoResponse = questionReader.readDetailInfo(question.getId());

        // then
        assertThat(questionDetailInfoResponse.getTitle()).isEqualTo("질문제목");
        assertThat(questionDetailInfoResponse.getAnswer().getContent()).isEqualTo("답변내용");

    }

    @DisplayName("질문 상세정보 요청 시 답변이 없다면 답변만 null로 반환한다")
    @Test
    void AnswerInQuestionDetailCanBeNull() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question question = createQuestion(store);
        questionRepository.save(question);

        String nickname = "유저명";
        String productName = "가게명";

        // when
        QuestionDetailInfoResponse questionDetailInfoResponse = questionReader.readDetailInfo(question.getId());

        // then
        assertThat(questionDetailInfoResponse.getAnswer()).isNull();
        assertThat(questionDetailInfoResponse.getTitle()).isEqualTo("질문제목");
    }

    @DisplayName("질문 상세정보 요청 시 해당 질문은 읽음처리된다")
    @Test
    void isReadWillTrueWhenReadDetailInfo() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question question = createQuestion(store);
        questionRepository.save(question);

        String nickname = "유저명";
        String productName = "가게명";

        // when
        questionReader.readDetailInfo(question.getId());

        em.flush();
        em.clear();

        Question savedQuestion = questionRepository.findById(question.getId()).get();

        // then
        assertThat(savedQuestion.getIsRead()).isTrue();

    }

    @DisplayName("isReplied가 null일 때 해당 가게의 모든 질문을 가져온다")
    @Test
    void readQuestionsForStoreOwner() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question q1 = createQuestion(store);
        Question q2 = createQuestion(store);
        Question q3 = createQuestion(store);
        Question q4 = createQuestion(store);
        Question q5 = createQuestion(store);
        questionRepository.saveAll(List.of(q1,q2,q3,q4,q5));

        Answer a1 = createAnswer(q1);
        Answer a2 = createAnswer(q2);
        Answer a3 = createAnswer(q3);
        answerRepository.saveAll(List.of(a1,a2,a3));

        PageRequest pageRequest = PageRequest.of(0, 10);

        Boolean isReplied = null;

        // when
        Page<QuestionForOwnerDto> result =
                questionReader.readQuestionsForStoreOwner(store.getId(), isReplied, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent()).extracting("isReplied")
                .containsExactlyInAnyOrder(
                        true,true,true,false,false
                );

    }

    @DisplayName("isReplied가 true일 때 해당 가게의 질문 중 답변한 질문만 가져온다")
    @Test
    void readRepliedQuestionsForStoreOwner() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question q1 = createQuestion(store);
        Question q2 = createQuestion(store);
        Question q3 = createQuestion(store);
        Question q4 = createQuestion(store);
        Question q5 = createQuestion(store);
        questionRepository.saveAll(List.of(q1,q2,q3,q4,q5));

        Answer a1 = createAnswer(q1);
        Answer a2 = createAnswer(q2);
        Answer a3 = createAnswer(q3);
        answerRepository.saveAll(List.of(a1,a2,a3));

        PageRequest pageRequest = PageRequest.of(0, 10);

        Boolean isReplied = true;

        // when
        Page<QuestionForOwnerDto> result =
                questionReader.readQuestionsForStoreOwner(store.getId(), isReplied, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);

    }

    @DisplayName("isReplied가 false일 때 해당 가게의 질문 중 답변하지 않은 질문만 가져온다")
    @Test
    void readNonRepliedQuestionsForStoreOwner() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question q1 = createQuestion(store);
        Question q2 = createQuestion(store);
        Question q3 = createQuestion(store);
        Question q4 = createQuestion(store);
        Question q5 = createQuestion(store);
        questionRepository.saveAll(List.of(q1,q2,q3,q4,q5));

        Answer a1 = createAnswer(q1);
        Answer a2 = createAnswer(q2);
        Answer a3 = createAnswer(q3);
        answerRepository.saveAll(List.of(a1,a2,a3));

        PageRequest pageRequest = PageRequest.of(0, 10);

        Boolean isReplied = false;

        // when
        Page<QuestionForOwnerDto> result =
                questionReader.readQuestionsForStoreOwner(store.getId(), isReplied, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);

    }

    @DisplayName("해당 상품에 달린 문의를 조회한다")
    @Test
    void readQuestionsInProduct() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question q1 = createQuestionWithProductIdAndUserId(store,"1",2L);
        Question q2 = createQuestionWithProductIdAndUserId(store,"1",2L);
        Question q3 = createQuestionWithProductIdAndUserId(store,"1",3L);
        Question q4 = createQuestionWithProductIdAndUserId(store,"2",3L);
        Question q5 = createQuestionWithProductIdAndUserId(store,"2",2L);
        questionRepository.saveAll(List.of(q1,q2,q3,q4,q5));

        String productId = "1";
        Long userId = 2L;
        Boolean isReplied = false;
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<QuestionInProductDto> result = questionReader.readQuestionsInProduct(userId, productId, isReplied, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @DisplayName("내가 작성하지 않은 비밀글은 제목과 내용이 비밀처리된다")
    @Test
    void cannotSeeOthersSecretQuesetion() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);
        long writerId = 1L;

        Question q1 = createQuestion(store,writerId,true);
        questionRepository.save(q1);

        long readerId = 2L;
        String productId = "1";
        Boolean isReplied = false;
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<QuestionInProductDto> questionInProductDtos = questionReader.readQuestionsInProduct(readerId, productId, isReplied, pageRequest);
        QuestionInProductDto result = questionInProductDtos.getContent().get(0);

        // then
        assertThat(result.getTitle()).isEqualTo("비밀글입니다");
        assertThat(result.getContent()).isEqualTo("");

    }

    @DisplayName("비회원은 비밀글을 볼 수 없다")
    @Test
    void nonMembersCannotSeeSecretQuestion() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);
        long writerId = 1L;

        Question q1 = createQuestion(store,writerId,true);
        questionRepository.save(q1);

        Long nonMemberId = null;
        String productId = "1";
        Boolean isReplied = false;
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<QuestionInProductDto> questionInProductDtos = questionReader.readQuestionsInProduct(nonMemberId, productId, isReplied, pageRequest);
        QuestionInProductDto result = questionInProductDtos.getContent().get(0);

        // then
        assertThat(result.getTitle()).isEqualTo("비밀글입니다");
        assertThat(result.getContent()).isEqualTo("");
    }




    @DisplayName("해당 상품에서 내가 남긴 문의만 모아본다")
    @Test
    void readMyQuestionsInProduct() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question q1 = createQuestionWithProductIdAndUserId(store,"1",2L);
        Question q2 = createQuestionWithProductIdAndUserId(store,"1",2L);
        Question q3 = createQuestionWithProductIdAndUserId(store,"1",3L);
        Question q4 = createQuestionWithProductIdAndUserId(store,"2",3L);
        Question q5 = createQuestionWithProductIdAndUserId(store,"2",2L);
        questionRepository.saveAll(List.of(q1,q2,q3,q4,q5));

        String productId = "1";
        Long userId = 2L;
        Boolean isReplied = false;
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<MyQuestionInMypageDto> result = questionReader.readMyQuestionsInProduct(userId, productId, isReplied, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);

    }

    @DisplayName("내가 남긴 모든 문의를 확인한다")
    @Test
    void readMyQuestions() {
        // given
        Store store = createStore(1L);
        storeRepository.save(store);

        Question q1 = createQuestionWithProductIdAndUserId(store,"1",2L);
        Question q2 = createQuestionWithProductIdAndUserId(store,"1",2L);
        Question q3 = createQuestionWithProductIdAndUserId(store,"1",3L);
        Question q4 = createQuestionWithProductIdAndUserId(store,"2",3L);
        Question q5 = createQuestionWithProductIdAndUserId(store,"2",2L);
        questionRepository.saveAll(List.of(q1,q2,q3,q4,q5));

        Long userId = 2L;
        Boolean isReplied = false;
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<MyQuestionInMypageDto> result = questionReader.readQuestionsForMypage(userId, isReplied, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);

    }


    private Question createQuestionWithProductIdAndUserId(Store store,String productId, Long userId) {
        return Question.builder()
                .store(store)
                .userId(userId)
                .nickname("닉네임")
                .productId(productId)
                .productName("상품명")
                .title("질문제목")
                .content("질문내용")
                .isSecret(true)
                .build();
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
    private Question createQuestion(Store store, Long writerId, boolean isSecret) {
        return Question.builder()
                .store(store)
                .userId(writerId)
                .nickname("닉네임")
                .productId("1")
                .productName("상품명")
                .title("질문제목")
                .content("질문내용")
                .isSecret(isSecret)
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