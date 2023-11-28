package kr.bb.store.domain.subscription.controller;

import kr.bb.store.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/stores")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("store-subscriptions")
    public ResponseEntity subscriptionsForMypage() {
        // TODO : requestHeader로 변경
        Long userId = 1L;

        return ResponseEntity.ok().body(subscriptionService.getSubscriptionsOfUser(userId));
    }

    @GetMapping("/{storeId}/store-subscriptions")
    public ResponseEntity subscriptionsForDate(@PathVariable Long storeId,
                                               @RequestParam String year, @RequestParam String month, @RequestParam String day) {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return ResponseEntity.ok().body(subscriptionService.getSubscriptionsForDate(storeId, date));

    }

}
