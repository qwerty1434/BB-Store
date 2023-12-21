package kr.bb.store.domain.store.controller;

import bloomingblooms.domain.order.ValidatePriceDto;
import bloomingblooms.domain.store.StoreInfoDto;
import bloomingblooms.domain.store.StoreNameAndAddressDto;
import bloomingblooms.response.CommonResponse;
import kr.bb.store.domain.store.facade.StoreFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/client/stores")
public class StoreFeignController {
    private final StoreFacade storeFacade;

    @GetMapping("/id")
    public CommonResponse<Long> getStoreId(@RequestHeader Long userId) {
        return CommonResponse.success(storeFacade.getStoreId(userId));
    }

    @GetMapping("/{storeId}/info")
    public CommonResponse<StoreNameAndAddressDto> getStoreNameAndAddress(@PathVariable Long storeId) {
        return CommonResponse.success(storeFacade.getStoreNameAndAddress(storeId));
    }

    @GetMapping("/{storeId}/name")
    public CommonResponse<String> getStoreName(@PathVariable Long storeId) {
        return CommonResponse.success(storeFacade.getStoreName(storeId));
    }

    @GetMapping("/{storeId}")
    public CommonResponse<StoreInfoDto> getStoreInfo(@PathVariable Long storeId) {
        return CommonResponse.success(storeFacade.getStoreInfo(storeId));
    }

    @GetMapping
    public CommonResponse<List<StoreInfoDto>> getStoreInfos() {
        return CommonResponse.success(storeFacade.getAllStoreInfos());
    }

    @PostMapping("/coupons/validate-purchase")
    public void validateForOrder(List<ValidatePriceDto> validatePriceDtos) {
        storeFacade.validateForOrder(validatePriceDtos);
    }


}
