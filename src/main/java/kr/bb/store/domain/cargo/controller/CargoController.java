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
        cargoService.modifyStock(storeId,stockModifyRequest.getStockModifyDtos());
        return ResponseEntity.ok().build();
    }
}
