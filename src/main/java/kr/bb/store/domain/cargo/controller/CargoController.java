package kr.bb.store.domain.cargo.controller;

import kr.bb.store.domain.cargo.controller.request.StockModifyRequest;
import kr.bb.store.domain.cargo.service.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class CargoController {
    private final CargoService cargoService;

    @PutMapping("/{storeId}/flowers/stock")
    public ResponseEntity modifyAllStocks(@PathVariable Long storeId,
                                          @RequestBody StockModifyRequest stockModifyRequest) {
        cargoService.modifyAllStocks(storeId,stockModifyRequest.getStockModifyDtos());
        return ResponseEntity.ok().build();
    }

    @GetMapping("{storeId}/fowers/stocks")
    public ResponseEntity getAllStocks(@PathVariable Long storeId) {
        return ResponseEntity.ok().body(cargoService.getAllStocks(storeId));
    }
}
