package kr.bb.store.domain.question.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyQuestionInProductDto {
    private Long key;
    private Boolean isReplied;
    private String title;
    private String content;
    private String nickname;
    private LocalDate createdAt;
    private String reply;
    private LocalDate repliedAt;

    @QueryProjection
    public MyQuestionInProductDto(Long key, Boolean isReplied, String title, String content, String nickname, LocalDateTime createdAt, String reply, LocalDateTime repliedAt) {
        this.key = key;
        this.isReplied = isReplied;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.createdAt = createdAt.toLocalDate();
        this.reply = reply;
        this.repliedAt  = repliedAt != null ? repliedAt.toLocalDate() : null;
    }
}
