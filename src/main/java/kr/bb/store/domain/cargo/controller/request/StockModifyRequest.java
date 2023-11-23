package kr.bb.store.domain.cargo.controller.request;

import kr.bb.store.domain.cargo.dto.StockModifyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockModifyRequest {
    List<StockModifyDto> stockModifyDtos;
}
