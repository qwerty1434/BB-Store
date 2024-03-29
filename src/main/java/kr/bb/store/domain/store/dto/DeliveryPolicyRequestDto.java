package kr.bb.store.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPolicyRequestDto {
    private Long deliveryPrice;
    private Long freeDeliveryMinPrice;
}