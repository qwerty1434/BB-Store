package kr.bb.store.domain.store.controller;


import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.controller.response.*;
import kr.bb.store.domain.store.dto.GugunDto;
import kr.bb.store.domain.store.dto.SidoDto;
import kr.bb.store.domain.store.facade.StoreFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreFacade storeFacade;

    @PostMapping
    public ResponseEntity<Long> createStore(@Valid @RequestBody StoreCreateRequest storeCreateRequest,
                                      @RequestHeader(value = "userId") Long userId) {
        return ResponseEntity.ok().body(storeFacade.createStore(userId, storeCreateRequest));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<Void> editStoreInfo(@PathVariable Long storeId,
                                        @Valid @RequestBody StoreInfoEditRequest storeInfoEditRequest) {
        storeFacade.editStoreInfo(storeId, storeInfoEditRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDetailInfoResponse> getStoreInfo(@PathVariable Long storeId) {
        return ResponseEntity.ok().body(storeFacade.getStoreDetailInfo(storeId));
    }

    @GetMapping("/list")
    public ResponseEntity<SimpleStorePagingResponse> getStores(@RequestHeader(value = "userId") Long userId,
                                                               Pageable pageable) {
        return ResponseEntity.ok().body(storeFacade.getStoresWithLikes(userId, pageable));
    }

    @GetMapping("/{storeId}/user")
    public ResponseEntity<StoreInfoUserResponse> getStoreInfoForUser(@RequestHeader(value = "userId") Long userId,
                                                                     @PathVariable Long storeId){
        return ResponseEntity.ok().body(storeFacade.getStoreInfoForUser(userId, storeId));
    }

    @GetMapping("/{storeId}/manager")
    public ResponseEntity<StoreInfoManagerResponse> getStoreInfoForManager(@PathVariable Long storeId){
        return ResponseEntity.ok().body(storeFacade.getStoreInfoForManager(storeId));
    }

    @GetMapping("/map/location")
    public ResponseEntity<StoreListForMapResponse> getNearbyStores(@RequestParam Double lat, @RequestParam Double lon,
                                                                   @RequestHeader(value = "userId") Long userId,
                                                                   @RequestParam Integer level) {
        return ResponseEntity.ok().body(storeFacade.getNearbyStores(userId, lat, lon, level));
    }

    @GetMapping("/map/region")
    public ResponseEntity<StoreListForMapResponse> getStoresWithRegion(@RequestParam String sido, @RequestParam String gugun,
                                                                       @RequestHeader(value = "userId") Long userId) {
        return ResponseEntity.ok().body(storeFacade.getStoresWithRegion(userId, sido, gugun));
    }

    @GetMapping("/address/sido")
    public ResponseEntity<List<SidoDto>> getSido() {
        return ResponseEntity.ok().body(storeFacade.getSido());
    }

    @GetMapping("/address/gugun")
    public ResponseEntity<List<GugunDto>> getGugun(@RequestParam String sido) {
        return ResponseEntity.ok().body(storeFacade.getGugun(sido));
    }
}
