package kr.bb.store.domain.cargo.controller.response;

import kr.bb.store.domain.cargo.dto.StockInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemainingStocksResponse {
    List<StockInfoDto> stockInfoDtos;
}
