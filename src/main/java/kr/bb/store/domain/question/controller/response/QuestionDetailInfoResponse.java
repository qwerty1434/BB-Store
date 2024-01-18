package kr.bb.store.domain.question.controller.response;

import kr.bb.store.domain.question.dto.AnswerDto;
import kr.bb.store.domain.question.entity.Answer;
import kr.bb.store.domain.question.entity.Question;
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

    public static QuestionDetailInfoResponse of(Question question, Answer answer) {
        return QuestionDetailInfoResponse.builder()
                .title(question.getTitle())
                .nickname(question.getNickname())
                .createdAt(question.getCreatedAt())
                .productName(question.getProductName())
                .content(question.getContent())
                .isReplied(answer != null)
                .answer(answer == null ? null : AnswerDto.fromEntity(answer))
                .build();
    }

}
