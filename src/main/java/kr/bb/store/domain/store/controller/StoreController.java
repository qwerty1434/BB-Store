package kr.bb.store.domain.store.controller;


import kr.bb.store.domain.cargo.dto.FlowerDto;
import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.controller.response.*;
import kr.bb.store.domain.store.dto.GugunDto;
import kr.bb.store.domain.store.dto.SidoDto;
import kr.bb.store.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<Long> createStore(@Valid @RequestBody StoreCreateRequest storeCreateRequest,
                                      @RequestHeader(value = "userId") Long userId) {
        // TODO : feign통신
        List<FlowerDto> flowers = new ArrayList<>();
        return ResponseEntity.ok().body(storeService.createStore(userId, storeCreateRequest, flowers));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<Void> editStoreInfo(@PathVariable Long storeId,
                                        @Valid @RequestBody StoreInfoEditRequest storeInfoEditRequest) {
        storeService.editStoreInfo(storeId, storeInfoEditRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDetailInfoResponse> getStoreInfo(@PathVariable Long storeId) {
        return ResponseEntity.ok().body(storeService.getStoreInfo(storeId));
    }

    @GetMapping("/list")
    public ResponseEntity<SimpleStorePagingResponse> getStores(@RequestHeader(value = "userId") Long userId,
                                                               Pageable pageable) {
        return ResponseEntity.ok().body(storeService.getStoresWithPaging(userId, pageable));
    }

    @GetMapping("/{storeId}/user")
    public ResponseEntity<StoreInfoUserResponse> getStoreInfoForUser(@RequestHeader(value = "userId") Long userId,
                                                                     @PathVariable Long storeId){
        return ResponseEntity.ok().body(storeService.getStoreInfoForUser(userId, storeId));
    }

    @GetMapping("/{storeId}/manager")
    public ResponseEntity<StoreInfoManagerResponse> getStoreInfoForManager(@PathVariable Long storeId){
        return ResponseEntity.ok().body(storeService.getStoreInfoForManager(storeId));
    }

    @GetMapping("/map/location")
    public ResponseEntity<StoreListForMapResponse> getNearbyStores(@RequestParam Double lat, @RequestParam Double lon,
                                                                   @RequestHeader(value = "userId") Long userId,
                                                                   @RequestParam Integer level) {
        return ResponseEntity.ok().body(storeService.getNearbyStores(userId, lat, lon, level));
    }

    @GetMapping("/map/region")
    public ResponseEntity<StoreListForMapResponse> getStoresWithRegion(@RequestParam String sido, @RequestParam String gugun,
                                                                       @RequestHeader(value = "userId") Long userId) {
        return ResponseEntity.ok().body(storeService.getStoresWithRegion(userId, sido, gugun));
    }

    @GetMapping("/address/sido")
    public ResponseEntity<List<SidoDto>> getSido() {
        return ResponseEntity.ok().body(storeService.getSido());
    }

    @GetMapping("/address/gugun")
    public ResponseEntity<List<GugunDto>> getGugun(@RequestParam String sido) {
        return ResponseEntity.ok().body(storeService.getGugun(sido));
    }
}
