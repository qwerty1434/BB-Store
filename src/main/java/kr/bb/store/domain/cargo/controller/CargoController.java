package kr.bb.store.domain.cargo.controller;

import bloomingblooms.response.CommonResponse;
import kr.bb.store.domain.cargo.controller.request.StockModifyRequest;
import kr.bb.store.domain.cargo.controller.response.RemainingStocksResponse;
import kr.bb.store.domain.cargo.facade.CargoFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CargoController {
    private final CargoFacade cargoFacade;

    @PutMapping("/{storeId}/flowers/stock")
    public void modifyAllStocks(@PathVariable Long storeId,
                                @RequestBody StockModifyRequest stockModifyRequest) {
        cargoFacade.modifyAllStocksWithLock(storeId,stockModifyRequest.getStockModifyDtos());
    }

    @GetMapping("/{storeId}/flowers/stock")
    public CommonResponse<RemainingStocksResponse> getAllStocks(@PathVariable Long storeId){
        return CommonResponse.success(cargoFacade.getAllStocks(storeId));
    }

}
