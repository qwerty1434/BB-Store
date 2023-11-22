package kr.bb.store.domain.store.controller;


import kr.bb.store.domain.store.controller.request.StoreInfoEditRequest;
import kr.bb.store.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity createStore() {
        // TODO : header값으로 바꾸기
        Long userId = 1L;
        storeService.createStore(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{storeId}")
    public ResponseEntity editStoreInfo(@PathVariable Long storeId,
                                        @RequestBody StoreInfoEditRequest storeInfoEditRequest) {
        storeService.editStoreInfo(storeId, storeInfoEditRequest);
        return ResponseEntity.ok().build();
    }
}
