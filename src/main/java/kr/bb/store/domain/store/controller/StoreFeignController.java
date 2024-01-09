package kr.bb.store.domain.store.controller;

import bloomingblooms.domain.order.ValidatePolicyDto;
import bloomingblooms.domain.store.StoreInfoDto;
import bloomingblooms.domain.store.StoreNameAndAddressDto;
import bloomingblooms.domain.store.StorePolicy;
import bloomingblooms.domain.wishlist.likes.LikedStoreInfoResponse;
import bloomingblooms.response.CommonResponse;
import kr.bb.store.domain.store.facade.StoreFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/store-name")
    public CommonResponse<Map<Long, String>> getStoreNames(@RequestParam List<Long> storeIds) {
        return CommonResponse.success(storeFacade.getStoreNames(storeIds));
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
    public CommonResponse validateForOrder(@RequestBody ValidatePolicyDto validatePolicyDto) {
        storeFacade.validateForOrder(validatePolicyDto);
        return CommonResponse.success(null);
    }

    @PostMapping("/simple-info")
    public CommonResponse<List<LikedStoreInfoResponse>> getStoreSimpleInfos(@RequestBody List<Long> storeIds) {
        return CommonResponse.success(storeFacade.simpleInfos(storeIds));
    }

    @PostMapping("/policy")
    public CommonResponse<Map<Long, StorePolicy>> getDeliveryPolicyOfStores(@RequestBody List<Long> storeIds) {
        return CommonResponse.success(storeFacade.getDeliveryPolicies(storeIds));
    }

}
