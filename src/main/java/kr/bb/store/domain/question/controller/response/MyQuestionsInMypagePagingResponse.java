package kr.bb.store.domain.question.controller.response;

import kr.bb.store.domain.question.dto.MyQuestionInMypageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyQuestionsInMypagePagingResponse {
    private List<MyQuestionInMypageDto> data;
    private Long totalCnt;

}
