package kr.bb.store.domain.store.controller;


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
}
