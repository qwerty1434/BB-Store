package kr.bb.store.domain.store.dto;

import kr.bb.store.domain.store.entity.DeliveryPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPolicyDto {
    private Long deliveryPrice;
    private Long freeDeliveryMinPrice;

    public static DeliveryPolicyDto fromEntity(DeliveryPolicy deliveryPolicy) {
        return DeliveryPolicyDto.builder()
                .deliveryPrice(deliveryPolicy.getDeliveryPrice())
                .freeDeliveryMinPrice(deliveryPolicy.getFreeDeliveryMinPrice())
                .build();
    }
}
