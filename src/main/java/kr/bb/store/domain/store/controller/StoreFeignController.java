package kr.bb.store.domain.store.controller;

import bloomingblooms.domain.store.StoreInfoDto;
import bloomingblooms.domain.store.StoreNameAndAddressDto;
import kr.bb.store.domain.store.facade.StoreFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/client/stores")
public class StoreFeignController {
    private final StoreFacade storeFacade;

    @GetMapping("/id")
    public ResponseEntity<Long> getStoreId(@RequestHeader Long userId) {
        return ResponseEntity.ok().body(storeFacade.getStoreId(userId));
    }

    @GetMapping("/{storeId}/info")
    public ResponseEntity<StoreNameAndAddressDto> getStoreNameAndAddress(@PathVariable Long storeId) {
        return ResponseEntity.ok().body(storeFacade.getStoreNameAndAddress(storeId));
    }

    @GetMapping("/{storeId}/name")
    public ResponseEntity<String> getStoreName(@PathVariable Long storeId) {
        return ResponseEntity.ok().body(storeFacade.getStoreName(storeId));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreInfoDto> getStoreInfo(@PathVariable Long storeId) {
        return ResponseEntity.ok().body(storeFacade.getStoreInfo(storeId));
    }

    @GetMapping
    public ResponseEntity<List<StoreInfoDto>> getStoreInfos() {
        return ResponseEntity.ok().body(storeFacade.getAllStoreInfos());
    }

}
