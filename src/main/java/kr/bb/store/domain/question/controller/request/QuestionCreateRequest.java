package kr.bb.store.domain.question.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreateRequest {
    private Long storeId;
    private String productName;
    private String productId;
    private String title;
    private String content;
    private Boolean isSecret;
    private String nickname;
}
