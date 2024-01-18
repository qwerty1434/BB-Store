package kr.bb.store.domain.store.controller.response;

import kr.bb.store.domain.store.dto.StoreForAdminDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreForAdminDtoResponse {
    private List<StoreForAdminDto> data;
    private Long totalCnt;

    public static StoreForAdminDtoResponse of(List<StoreForAdminDto> data, Long totalCnt) {
        return StoreForAdminDtoResponse.builder()
                .data(data)
                .totalCnt(totalCnt)
                .build();
    }
}
