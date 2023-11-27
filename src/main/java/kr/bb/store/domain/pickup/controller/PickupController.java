package kr.bb.store.domain.pickup.controller;

import kr.bb.store.domain.pickup.service.PickupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class PickupController {
    private final PickupService pickupService;

    @GetMapping("/reservations")
    public ResponseEntity myPickups(Pageable pageable) {
        // TODO : requestHeader로 변경
        Long userId = 1L;

        return ResponseEntity.ok().body(pickupService.getPickupsForUser(userId, pageable));
    }
}
