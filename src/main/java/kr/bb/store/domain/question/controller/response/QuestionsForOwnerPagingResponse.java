package kr.bb.store.domain.question.controller.response;

import kr.bb.store.domain.question.dto.QuestionForOwnerDto;
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
public class QuestionsForOwnerPagingResponse {
    private List<QuestionForOwnerDto> data;
    private Long totalCnt;

    public static QuestionsForOwnerPagingResponse from(Page<QuestionForOwnerDto> questionForOwnerDtos) {
        return QuestionsForOwnerPagingResponse.builder()
                .data(questionForOwnerDtos.getContent())
                .totalCnt(questionForOwnerDtos.getTotalElements())
                .build();
    }
}
