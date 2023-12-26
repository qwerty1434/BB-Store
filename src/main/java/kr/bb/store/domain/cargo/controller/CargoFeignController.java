package kr.bb.store.domain.cargo.controller;

import bloomingblooms.domain.flower.StockChangeDto;
import kr.bb.store.domain.cargo.facade.CargoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/client/stores/flowers/stocks")
public class CargoFeignController {
    private final CargoFacade cargoFacade;
    @PutMapping("/add")
    public void addStock(@RequestHeader(value = "userId") Long userId, @RequestBody StockChangeDto stockChangeDto) {
        cargoFacade.plusStockCountsWithLock(userId, stockChangeDto);
    }

    @PutMapping("/substract")
    public void subtractStock(@RequestHeader(value = "userId") Long userId, @RequestBody StockChangeDto stockChangeDto) {
        cargoFacade.minusStockCountsWithLock(userId, stockChangeDto);
    }
}
