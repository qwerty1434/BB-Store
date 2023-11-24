package kr.bb.store.domain.question.dto;

import kr.bb.store.domain.question.entity.Answer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    private String content;
    private LocalDate repliedAt;

    public static AnswerDto fromEntity(Answer answer) {
        return AnswerDto.builder()
                .content(answer.getContent())
                .repliedAt(answer.getUpdatedAt().toLocalDate())
                .build();
    }
}
