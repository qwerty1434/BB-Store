package kr.bb.store.domain.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionForUserDto {
    private Long storeSubscriptionId;
    private String subscriptionProductName;
    private String subscriptionProductThumbnail;
    private Integer paymentDay;

}
