package kr.bb.store.domain.pickup.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupCreateRequest {
    private Long storeId;
    private Long userId;
    private Long orderPickupId;
    private Long productId;
    private LocalDate pickupDate;
    private String pickupTime;
}
