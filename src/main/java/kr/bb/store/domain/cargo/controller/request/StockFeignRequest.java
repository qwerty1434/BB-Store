package kr.bb.store.domain.cargo.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockFeignRequest {
    private Long storeId;
    private Long flowerId;
    private Long stock;
}
