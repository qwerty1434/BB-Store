package kr.bb.store.domain.question.controller.response;

import kr.bb.store.domain.question.dto.QuestionInProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsInProductPagingResponse {
    private List<QuestionInProductDto> data;
    private Long totalCnt;

    public static QuestionsInProductPagingResponse from(Page<QuestionInProductDto> questionInProductDtos) {
        return QuestionsInProductPagingResponse.builder()
                .data(questionInProductDtos.getContent())
                .totalCnt(questionInProductDtos.getTotalElements())
                .build();
    }
}
