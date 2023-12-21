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
    public void addStock(@RequestBody StockChangeDto stockChangeDto) {
        cargoFacade.plusStockCountWithLock(stockChangeDto.getStoreId(), stockChangeDto.getFlowerId(), stockChangeDto.getStock());
    }

    @PutMapping("/substract")
    public void subtractStock(@RequestBody StockChangeDto stockChangeDto) {
        cargoFacade.minusStockCountWithLock(stockChangeDto.getStoreId(), stockChangeDto.getFlowerId(), stockChangeDto.getStock());
    }
}
