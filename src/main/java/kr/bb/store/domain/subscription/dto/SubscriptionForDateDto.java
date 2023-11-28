package kr.bb.store.domain.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionForDateDto {
    private Long storeSubscriptionId;
    private String subscriptionCode;
    private String productName;
    private String productThumbnailImage;
    private String deliveryRecipientName;
    private String deliveryRecipientPhoneNumber;
    private String deliveryRoadName;
    private String deliveryAddressDetail;
    private Long productPrice;


}
