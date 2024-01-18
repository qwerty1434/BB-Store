package kr.bb.store.domain.question.controller.response;

import kr.bb.store.domain.question.dto.MyQuestionInMypageDto;
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
public class MyQuestionsInProductPagingResponse {
    private List<MyQuestionInMypageDto> data;
    private Long totalCnt;

    public static MyQuestionsInProductPagingResponse from(Page<MyQuestionInMypageDto> questionInProductDtos) {
        return MyQuestionsInProductPagingResponse.builder()
                .data(questionInProductDtos.getContent())
                .totalCnt(questionInProductDtos.getTotalElements())
                .build();
    }
}
