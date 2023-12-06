package kr.bb.store.domain.cargo.controller;

import kr.bb.store.domain.cargo.controller.request.StockFeignRequest;
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
    public ResponseEntity<Void> addStock(@RequestBody StockFeignRequest stockFeignRequest) {
        cargoService.plusStockCount(stockFeignRequest.getStoreId(), stockFeignRequest.getFlowerId(), stockFeignRequest.getStock());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/substract")
    public ResponseEntity<Void> subtractStock(@RequestBody StockFeignRequest stockFeignRequest) {
        cargoService.minusStockCount(stockFeignRequest.getStoreId(), stockFeignRequest.getFlowerId(), stockFeignRequest.getStock());
        return ResponseEntity.ok().build();
    }
}
