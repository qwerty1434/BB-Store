package kr.bb.store.domain.store.controller;

import kr.bb.store.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreFeignController {
    private final StoreService storeService;

    @GetMapping("/id")
    public ResponseEntity<Long> getStoreId(@RequestHeader Long userId) {
        return ResponseEntity.ok().body(storeService.getStoreId(userId));
    }
}
