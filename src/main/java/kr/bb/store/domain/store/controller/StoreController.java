package kr.bb.store.domain.store.controller;


import kr.bb.store.domain.store.controller.request.StoreCreateRequest;
import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity createStore(@Valid @RequestBody StoreCreateRequest storeCreateRequest) {
        // TODO : header값으로 바꾸기
        Long userId = 1L;
        return ResponseEntity.ok().body(storeService.createStore(userId, storeCreateRequest));
    }

    @PutMapping("/{storeId}")
    public ResponseEntity editStoreInfo(@PathVariable Long storeId,
                                        @Valid @RequestBody StoreInfoEditRequest storeInfoEditRequest) {
        storeService.editStoreInfo(storeId, storeInfoEditRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{storeId}")
    public ResponseEntity getStoreInfo(@PathVariable Long storeId) {
        return ResponseEntity.ok().body(storeService.getStoreInfo(storeId));
    }

    @GetMapping("/list")
    public ResponseEntity getStores(Pageable pageable) {
        return ResponseEntity.ok().body(storeService.getStoresWithPaging(pageable));
    }

    @GetMapping("/{storeId}/user")
    public ResponseEntity getStoreInfoForUser(@PathVariable Long storeId){
        return ResponseEntity.ok().body(storeService.getStoreInfoForUser(storeId));
    }

    @GetMapping("/{storeId}/manager")
    public ResponseEntity getStoreInfoForManager(@PathVariable Long storeId){
        return ResponseEntity.ok().body(storeService.getStoreInfoForManager(storeId));
    }

    @GetMapping("/map/location")
    public ResponseEntity getNearbyStores(@RequestParam Double lat, @RequestParam Double lon) {
        return ResponseEntity.ok().body(storeService.getNearbyStores(lat,lon));
    }

    @GetMapping("/map/region")
    public ResponseEntity getStoresWithRegion(@RequestParam String sido, @RequestParam String gugun) {
        return ResponseEntity.ok().body(storeService.getStoresWithRegion(sido,gugun));
    }
}