package kr.bb.store.domain.cargo.controller;

import kr.bb.store.domain.cargo.controller.request.StockModifyRequest;
import kr.bb.store.domain.cargo.facade.CargoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CargoController {
    private final CargoFacade cargoFacade;

    @PutMapping("/{storeId}/flowers/stock")
    public void modifyAllStocks(@PathVariable Long storeId,
                                          @RequestBody StockModifyRequest stockModifyRequest) {
        cargoFacade.modifyAllStocksWithLock(storeId,stockModifyRequest.getStockModifyDtos());
    }

}
