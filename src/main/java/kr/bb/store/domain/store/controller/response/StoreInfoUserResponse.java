package kr.bb.store.domain.store.controller.response;

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
public class StoreInfoUserResponse {
    private String storeName;
    private String storeThumbnailImage;
    private String address;
    private String detailAddress;
    private Double averageRating;
    private String detailInfo;
    private String phoneNumber;
    private Boolean isLiked;
    private Boolean isSubscribed;
    private String subscriptionProductId;

    public static StoreInfoUserResponse of(Store store, StoreAddress storeAddress, Boolean isLiked,
                                           Boolean isSubscribed, String subscriptionProductId) {
        return StoreInfoUserResponse.builder()
                .storeName(store.getStoreName())
                .storeThumbnailImage(store.getStoreThumbnailImage())
                .address(storeAddress.getAddress())
                .detailAddress(storeAddress.getDetailAddress())
                .averageRating(store.getAverageRating())
                .detailInfo(store.getDetailInfo())
                .phoneNumber(store.getPhoneNumber())
                .isLiked(isLiked)
                .isSubscribed(isSubscribed)
                .subscriptionProductId(subscriptionProductId)
                .build();
    }
}
