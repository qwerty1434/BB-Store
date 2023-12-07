package kr.bb.store.domain.question.controller;

import kr.bb.store.domain.question.controller.request.AnswerCreateRequest;
import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.controller.response.*;
import kr.bb.store.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("/questions")
    public ResponseEntity<Void> createQuestion(@RequestBody QuestionCreateRequest questionCreateRequest,
                                         @RequestHeader(value = "userId") Long userId) {
        questionService.createQuestion(userId, questionCreateRequest);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/questions/{questionId}")
    public ResponseEntity<QuestionDetailInfoResponse> getQuestionDetail(@PathVariable Long questionId) {
        return ResponseEntity.ok().body(questionService.getQuestionInfo(questionId));
    }

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<Void> createAnswer(@PathVariable Long questionId,
                                         @RequestBody AnswerCreateRequest answerCreateRequest) {
        questionService.createAnswer(questionId, answerCreateRequest.getContent());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{storeId}/questions")
    public ResponseEntity<QuestionsForOwnerPagingResponse> storeQuestions(@PathVariable Long storeId,
                                                                          @RequestParam(required = false) Boolean isReplied,
                                                                          Pageable pageable) {
        return ResponseEntity.ok().body(questionService.getQuestionsForStoreOwner(storeId,isReplied,pageable));
    }

    @GetMapping("/questions/product/{productId}")
    public ResponseEntity<QuestionsInProductPagingResponse> productQuestions(@PathVariable String productId,
                                                                             @RequestParam(required = false) Boolean isReplied,
                                                                             Pageable pageable,
                                                                             @RequestHeader(value = "userId", required = false) Long userId) {
        return ResponseEntity.ok().body(questionService.getQuestionsInProduct(userId, productId, isReplied, pageable));
    }

    @GetMapping("/questions/product/{productId}/my")
    public ResponseEntity<MyQuestionsInProductPagingResponse> myQuestionsInProduct(@PathVariable String productId,
                                                                                   @RequestParam(required = false) Boolean isReplied,
                                                                                   Pageable pageable,
                                                                                   @RequestHeader(value = "userId") Long userId) {
        return ResponseEntity.ok().body(questionService.getMyQuestionsInProduct(userId, productId, isReplied, pageable));
    }

    @GetMapping("/questions/my-page")
    public ResponseEntity<MyQuestionsInMypagePagingResponse> myQuestions(@RequestParam(required = false) Boolean isReplied,
                                                                         Pageable pageable,
                                                                         @RequestHeader(value = "userId") Long userId) {
        return ResponseEntity.ok().body(questionService.getMyQuestions(userId, isReplied, pageable));
    }

}
