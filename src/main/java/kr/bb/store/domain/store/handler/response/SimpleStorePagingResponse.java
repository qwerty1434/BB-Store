package kr.bb.store.domain.store.handler.response;

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
    private List<SimpleStoreResponse> simpleStores;
    private Integer totalCnt;
}
