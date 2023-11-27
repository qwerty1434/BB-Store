package kr.bb.store.domain.pickup.controller.response;

import kr.bb.store.domain.pickup.dto.PickupsInMypageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupsInMypageWithPageingResponse {
    private List<PickupsInMypageDto> data;
    private Long totalCnt;
}
