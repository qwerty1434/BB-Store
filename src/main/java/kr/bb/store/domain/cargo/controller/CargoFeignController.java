package kr.bb.store.domain.cargo.controller;

import bloomingblooms.domain.flower.StockChangeDto;
import kr.bb.store.domain.cargo.service.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/stores/flowers/stocks")
public class CargoFeignController {
    private final CargoService cargoService;
    @PutMapping("/add")
    public ResponseEntity<Void> addStock(@RequestBody StockChangeDto stockChangeDto) {
        cargoService.plusStockCount(stockChangeDto.getStoreId(), stockChangeDto.getFlowerId(), stockChangeDto.getStock());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/substract")
    public ResponseEntity<Void> subtractStock(@RequestBody StockChangeDto stockChangeDto) {
        cargoService.minusStockCount(stockChangeDto.getStoreId(), stockChangeDto.getFlowerId(), stockChangeDto.getStock());
        return ResponseEntity.ok().build();
    }
}
