package kr.bb.store.domain.store.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PositiveOrZero;

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

    @PositiveOrZero(message = "minOrderPrice cannot be negative")
    private Long minOrderPrice;
    @PositiveOrZero(message = "deliveryPrice cannot be negative")
    private Long deliveryPrice;
    @PositiveOrZero(message = "freeDeliveryMinPrice cannot be negative")
    private Long freeDeliveryMinPrice;

    private String sido;
    private String gugun;
    private String address;
    private String detailAddress;
    private String zipCode;
    private Float lat;
    private Float lon;
}
