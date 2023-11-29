package kr.bb.store.domain.pickup.controller;

import kr.bb.store.domain.pickup.controller.request.PickupCreateRequest;
import kr.bb.store.domain.pickup.service.PickupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/stores/reservations")
public class PickupFeignController {
    private final PickupService pickupService;

    @PostMapping
    public ResponseEntity<Void> createPickup(@RequestBody PickupCreateRequest pickupCreateRequest) {
        pickupService.createPickup(pickupCreateRequest);

        return ResponseEntity.ok().build();
    }
}
