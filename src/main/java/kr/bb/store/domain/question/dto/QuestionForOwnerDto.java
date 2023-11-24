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
public class QuestionForOwnerDto {
    private String key;
    private String productName;
    private String nickname;
    private String title;
    private LocalDate createdAt;
    private Boolean isReplied;
    private Boolean isRead;

    @QueryProjection
    public QuestionForOwnerDto(Long key, String title, LocalDateTime createdAt, Boolean isReplied, Boolean isRead) {
        this.key = key.toString();
        this.title = title;
        this.createdAt = createdAt.toLocalDate();
        this.isReplied = isReplied;
        this.isRead = isRead;
    }
}
