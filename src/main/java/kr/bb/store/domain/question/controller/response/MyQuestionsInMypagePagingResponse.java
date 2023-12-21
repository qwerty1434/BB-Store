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
public class MyQuestionsInMypagePagingResponse {
    private List<MyQuestionInMypageDto> data;
    private Long totalCnt;

    public static MyQuestionsInMypagePagingResponse from(Page<MyQuestionInMypageDto> myQuestionInMypageDtos) {
        return MyQuestionsInMypagePagingResponse.builder()
                .data(myQuestionInMypageDtos.getContent())
                .totalCnt(myQuestionInMypageDtos.getTotalElements())
                .build();

    }

}
