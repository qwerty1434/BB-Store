package kr.bb.store.domain.store.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreInfoEditRequest {
    private String storeName;
    private String detailInfo;
    private String storeThumbnailImage;
    private String phoneNumber;
    private String accountNumber;
    private String bank;

    private Long minOrderPrice;
    private Long deliveryPrice;
    private Long freeDeliveryMinPrice;

    private String sido;
    private String gugun;
    private String address;
    private String detailAddress;
    private String zipCode;
    private Float lat;
    private Float lon;
}
