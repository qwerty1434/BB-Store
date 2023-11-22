package kr.bb.store.domain.store.handler.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPolicyRequest {
    private Long minOrderPrice;
    private Long deliveryPrice;
    private Long freeDeliveryMinPrice;
}