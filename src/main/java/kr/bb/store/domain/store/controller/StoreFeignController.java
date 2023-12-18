package kr.bb.store.domain.store.controller;

import kr.bb.store.client.dto.StoreInfoDto;
import kr.bb.store.domain.store.facade.StoreFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreFeignController {
    private final StoreFacade storeFacade;

    @GetMapping("/id")
    public ResponseEntity<Long> getStoreId(@RequestHeader Long userId) {
        return ResponseEntity.ok().body(storeFacade.getStoreId(userId));
    }

    @GetMapping("/{storeId}/info")
    public ResponseEntity<StoreInfoDto> getStoreNameAndAddress(@PathVariable Long storeId) {
        return ResponseEntity.ok().body(storeFacade.getStoreNameAndAddress(storeId));
    }

}
