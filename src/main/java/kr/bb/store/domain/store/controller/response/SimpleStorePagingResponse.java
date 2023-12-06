package kr.bb.store.domain.store.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleStorePagingResponse {
    private List<StoreListResponse> stores;
    private Long totalCnt;
}
