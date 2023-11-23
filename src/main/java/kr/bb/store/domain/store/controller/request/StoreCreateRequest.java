package kr.bb.store.domain.store.controller.request;

import kr.bb.store.domain.store.handler.request.DeliveryPolicyRequest;
import kr.bb.store.domain.store.handler.request.StoreAddressRequest;
import kr.bb.store.domain.store.handler.request.StoreRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreCreateRequest {
    @NotNull(message = "storeName cannot be null")
    private String storeName;
    @NotNull(message = "detailInfo cannot be null")
    private String detailInfo;
    @NotNull(message = "storeThumbnailImage cannot be null")
    private String storeThumbnailImage;
    @NotNull(message = "phoneNumber cannot be null")
    private String phoneNumber;
    @NotNull(message = "accountNumber cannot be null")
    private String accountNumber;
    @NotNull(message = "bank cannot be null")
    private String bank;

    @PositiveOrZero(message = "minOrderPrice cannot be negative")
    private Long minOrderPrice;
    @PositiveOrZero(message = "deliveryPrice cannot be negative")
    private Long deliveryPrice;
    @PositiveOrZero(message = "freeDeliveryMinPrice cannot be negative")
    private Long freeDeliveryMinPrice;

    @NotNull(message = "sido cannot be null")
    private String sido;
    @NotNull(message = "gugun cannot be null")
    private String gugun;
    @NotNull(message = "address cannot be null")
    private String address;
    @NotNull(message = "detailAddress cannot be null")
    private String detailAddress;
    @NotNull(message = "zipCode cannot be null")
    private String zipCode;
    @NotNull(message = "lat cannot be null")
    private Double lat;
    @NotNull(message = "lon cannot be null")
    private Double lon;

    public StoreRequest toStoreRequest() {
        return StoreRequest.builder()
                .storeName(storeName)
                .detailInfo(detailInfo)
                .storeThumbnailImage(storeThumbnailImage)
                .phoneNumber(phoneNumber)
                .accountNumber(accountNumber)
                .bank(bank)
                .build();
    }
    public DeliveryPolicyRequest toDeliveryPolicyRequest() {
        return DeliveryPolicyRequest.builder()
                .minOrderPrice(minOrderPrice)
                .deliveryPrice(deliveryPrice)
                .freeDeliveryMinPrice(freeDeliveryMinPrice)
                .build();

    }
    public StoreAddressRequest toStoreAddressRequest() {
        return StoreAddressRequest.builder()
                .sido(sido)
                .gugun(gugun)
                .address(address)
                .detailAddress(detailAddress)
                .zipCode(zipCode)
                .lat(lat)
                .lon(lon)
                .build();
    }
}
