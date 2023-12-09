package kr.bb.store.domain.cargo.controller;

import kr.bb.store.domain.cargo.controller.request.StockModifyRequest;
import kr.bb.store.domain.cargo.controller.response.RemainingStocksResponse;
import kr.bb.store.domain.cargo.service.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CargoController {
    private final CargoService cargoService;

    @PutMapping("/{storeId}/flowers/stock")
    public ResponseEntity<Void> modifyAllStocks(@PathVariable Long storeId,
                                          @RequestBody StockModifyRequest stockModifyRequest) {
        cargoService.modifyAllStocks(storeId,stockModifyRequest.getStockModifyDtos());
        return ResponseEntity.ok().build();
    }

    @GetMapping("{storeId}/flowers/stocks")
    public ResponseEntity<RemainingStocksResponse> getAllStocks(@PathVariable Long storeId) {
        return ResponseEntity.ok().body(cargoService.getAllStocks(storeId));
    }
}
