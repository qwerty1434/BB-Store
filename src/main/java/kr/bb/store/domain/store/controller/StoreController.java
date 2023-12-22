package kr.bb.store.domain.store.controller;


import bloomingblooms.response.CommonResponse;
import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.controller.response.*;
import kr.bb.store.domain.store.dto.GugunDto;
import kr.bb.store.domain.store.dto.SidoDto;
import kr.bb.store.domain.store.facade.StoreFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreFacade storeFacade;

    @PostMapping
    public CommonResponse<Long> createStore(@Valid @RequestBody StoreCreateRequest storeCreateRequest,
                                            @RequestHeader(value = "userId") Long userId) {
        return CommonResponse.success(storeFacade.createStore(userId, storeCreateRequest));
    }

    @PutMapping("/{storeId}")
    public void editStoreInfo(@PathVariable Long storeId,
                                        @Valid @RequestBody StoreInfoEditRequest storeInfoEditRequest) {
        storeFacade.editStoreInfo(storeId, storeInfoEditRequest);
    }

    @GetMapping("/{storeId}")
    public CommonResponse<StoreDetailInfoResponse> getStoreInfo(@PathVariable Long storeId) {
        return CommonResponse.success(storeFacade.getStoreDetailInfo(storeId));
    }

    @GetMapping("/list")
    public CommonResponse<SimpleStorePagingResponse> getStores(
            @RequestHeader(value = "userId", required = false) Long userId, Pageable pageable) {
        return CommonResponse.success(storeFacade.getStoresWithLikes(userId, pageable));
    }

    @GetMapping("/{storeId}/user")
    public CommonResponse<StoreInfoUserResponse> getStoreInfoForUser(
            @RequestHeader(value = "userId", required = false) Long userId, @PathVariable Long storeId){
        return CommonResponse.success(storeFacade.getStoreInfoForUser(userId, storeId));
    }

    @GetMapping("/{storeId}/manager")
    public CommonResponse<StoreInfoManagerResponse> getStoreInfoForManager(@PathVariable Long storeId){
        return CommonResponse.success(storeFacade.getStoreInfoForManager(storeId));
    }

    @GetMapping("/map/location")
    public CommonResponse<StoreListForMapResponse> getNearbyStores(
            @RequestParam Double lat, @RequestParam Double lon,
            @RequestHeader(value = "userId", required = false) Long userId,@RequestParam Integer level) {
        return CommonResponse.success(storeFacade.getNearbyStores(userId, lat, lon, level));
    }

    @GetMapping("/map/region")
    public CommonResponse<StoreListForMapResponse> getStoresWithRegion(
            @RequestParam String sido, @RequestParam String gugun,
            @RequestHeader(value = "userId", required = false) Long userId) {
        return CommonResponse.success(storeFacade.getStoresWithRegion(userId, sido, gugun));
    }

    @GetMapping("/address/sido")
    public CommonResponse<List<SidoDto>> getSido() {
        return CommonResponse.success(storeFacade.getSido());
    }

    @GetMapping("/address/gugun")
    public CommonResponse<List<GugunDto>> getGugun(@RequestParam String sido) {
        return CommonResponse.success(storeFacade.getGugun(sido));
    }
}
