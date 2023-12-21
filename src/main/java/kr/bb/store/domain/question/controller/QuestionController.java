package kr.bb.store.domain.question.controller;

import bloomingblooms.response.CommonResponse;
import kr.bb.store.domain.question.controller.request.AnswerCreateRequest;
import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.controller.response.*;
import kr.bb.store.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("/questions")
    public void createQuestion(@RequestBody QuestionCreateRequest questionCreateRequest,
                                         @RequestHeader(value = "userId") Long userId) {
        questionService.createQuestion(userId, questionCreateRequest);
    }

    @GetMapping("/questions/{questionId}")
    public CommonResponse<QuestionDetailInfoResponse> getQuestionDetail(@PathVariable Long questionId) {
        return CommonResponse.success(questionService.getQuestionInfo(questionId));
    }

    @PostMapping("/questions/{questionId}/answers")
    public void createAnswer(@PathVariable Long questionId,
                                         @RequestBody AnswerCreateRequest answerCreateRequest) {
        questionService.createAnswer(questionId, answerCreateRequest.getContent());
    }

    @GetMapping("/{storeId}/questions")
    public CommonResponse<QuestionsForOwnerPagingResponse> storeQuestions(@PathVariable Long storeId,
                                                                          @RequestParam(required = false) Boolean isReplied,
                                                                          Pageable pageable) {
        return CommonResponse.success(questionService.getQuestionsForStoreOwner(storeId,isReplied,pageable));
    }

    @GetMapping("/questions/product/{productId}")
    public CommonResponse<QuestionsInProductPagingResponse> productQuestions(@PathVariable String productId,
                                                                             @RequestParam(required = false) Boolean isReplied,
                                                                             Pageable pageable,
                                                                             @RequestHeader(value = "userId", required = false) Long userId) {
        return CommonResponse.success(questionService.getQuestionsInProduct(userId, productId, isReplied, pageable));
    }

    @GetMapping("/questions/product/{productId}/my")
    public CommonResponse<MyQuestionsInProductPagingResponse> myQuestionsInProduct(@PathVariable String productId,
                                                                                   @RequestParam(required = false) Boolean isReplied,
                                                                                   Pageable pageable,
                                                                                   @RequestHeader(value = "userId") Long userId) {
        return CommonResponse.success(questionService.getMyQuestionsInProduct(userId, productId, isReplied, pageable));
    }

    @GetMapping("/questions/my-page")
    public CommonResponse<MyQuestionsInMypagePagingResponse> myQuestions(@RequestParam(required = false) Boolean isReplied,
                                                                         Pageable pageable,
                                                                         @RequestHeader(value = "userId") Long userId) {
        return CommonResponse.success(questionService.getMyQuestions(userId, isReplied, pageable));
    }

}
