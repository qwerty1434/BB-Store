package kr.bb.store.domain.store.controller.response;

import kr.bb.store.domain.store.entity.DeliveryPolicy;
import kr.bb.store.domain.store.entity.Store;
import kr.bb.store.domain.store.entity.StoreAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDetailInfoResponse {
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
    private Double lat;
    private Double lon;

    public static StoreDetailInfoResponse of(Store store, DeliveryPolicy deliveryPolicy, StoreAddress storeAddress) {
        return StoreDetailInfoResponse.builder()
                .storeName(store.getStoreName())
                .detailInfo(store.getDetailInfo())
                .storeThumbnailImage(store.getStoreThumbnailImage())
                .phoneNumber(store.getPhoneNumber())
                .accountNumber(store.getAccountNumber())
                .bank(store.getBank())
                .minOrderPrice(deliveryPolicy.getMinOrderPrice())
                .deliveryPrice(deliveryPolicy.getDeliveryPrice())
                .freeDeliveryMinPrice(deliveryPolicy.getFreeDeliveryMinPrice())
                .sido(storeAddress.getSido().getName())
                .gugun(storeAddress.getGugun().getName())
                .address(storeAddress.getAddress())
                .detailAddress(storeAddress.getDetailAddress())
                .zipCode(storeAddress.getZipCode())
                .lat(storeAddress.getLat())
                .lon(storeAddress.getLon())
                .build();
    }
}
