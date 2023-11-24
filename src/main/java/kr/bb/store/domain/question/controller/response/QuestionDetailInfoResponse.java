package kr.bb.store.domain.question.controller.response;

import kr.bb.store.domain.question.controller.dto.AnswerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDetailInfoResponse {
    private String title;
    private String nickname;
    private LocalDateTime createdAt;
    private String productName;
    private String content;
    private Boolean isReplied;
    private AnswerDto answer;

}
