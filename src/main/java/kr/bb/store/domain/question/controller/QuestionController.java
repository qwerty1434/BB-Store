package kr.bb.store.domain.question.controller;

import kr.bb.store.domain.question.controller.request.QuestionCreateRequest;
import kr.bb.store.domain.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("/questions")
    public ResponseEntity createQuestion(@RequestBody QuestionCreateRequest questionCreateRequest) {
        // TODO : header값으로 바꾸기
        Long userId = 1L;
        questionService.createQuestion(userId, questionCreateRequest);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/questions/{questionId}")
    public ResponseEntity getQuestionDetail(@PathVariable Long questionId) {
        return ResponseEntity.ok().body(questionService.getQuestionInfo(questionId));
    }
}
