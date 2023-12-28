package kr.bb.store.domain.cargo.controller;

import bloomingblooms.domain.flower.StockChangeDto;
import kr.bb.store.domain.cargo.facade.CargoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/client/stores/flowers/stocks")
public class CargoFeignController {
    private final CargoFacade cargoFacade;
    @PutMapping("/add")
    public void addStock(@RequestHeader(value = "userId") Long userId, @RequestBody List<StockChangeDto> stockChangeDtos) {
        cargoFacade.plusStocksWithLock(userId, stockChangeDtos);
    }

    @PutMapping("/subtract")
    public void subtractStock(@RequestHeader(value = "userId") Long userId, @RequestBody List<StockChangeDto> stockChangeDtos) {
        cargoFacade.minusStocksWithLock(userId, stockChangeDtos);
    }
}
