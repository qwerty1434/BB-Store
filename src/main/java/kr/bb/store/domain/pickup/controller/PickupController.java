package kr.bb.store.domain.pickup.controller;

import kr.bb.store.domain.pickup.service.PickupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/{storeId}/reservations/subscriptions")
    public ResponseEntity pickAndSubForCalendar(@PathVariable Long storeId) {
        // TODO : payment와 feign통신
        List<String> subscriptionDates = List.of("2023-11-27","2023-11-28");

        return ResponseEntity.ok().body(pickupService.getDataForCalendar(storeId, subscriptionDates));
    }

    @GetMapping("/{storeId}/reservations")
    public ResponseEntity pickupsForDate(@PathVariable Long storeId,
                                         @RequestParam String year, @RequestParam String month, @RequestParam String day) {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return ResponseEntity.ok().body(pickupService.getPickupsForDate(storeId, date));
    }
}
