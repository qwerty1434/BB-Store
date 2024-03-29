package kr.bb.store.domain.cargo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockModifyDto {
    private Long flowerId;
    private Long stock;
}
