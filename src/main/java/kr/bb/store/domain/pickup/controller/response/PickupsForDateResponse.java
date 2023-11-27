package kr.bb.store.domain.pickup.controller.response;

import kr.bb.store.domain.pickup.dto.PickupsForDateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupsForDateResponse {
    private List<PickupsForDateDto> data;
}
