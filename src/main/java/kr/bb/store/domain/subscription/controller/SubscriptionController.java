package kr.bb.store.domain.subscription.controller;

import kr.bb.store.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/stores")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity subscriptionsForMypage() {
        // TODO : requestHeader로 변경
        Long userId = 1L;

        return ResponseEntity.ok().body(subscriptionService.getSubscriptionsOfUser(userId));
    }

}
