package kr.bb.store.domain.store.dto;

import kr.bb.store.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikedStoreInfoResponse {
    private Long storeId;
    private String storeName;
    private String detailInfo;
    private Float averageRating;

    public static LikedStoreInfoResponse fromEntity(Store store) {
        return LikedStoreInfoResponse.builder()
                .storeId(store.getId())
                .storeName(store.getStoreName())
                .detailInfo(store.getDetailInfo())
                .averageRating(store.getAverageRating().floatValue())
                .build();
    }

    public bloomingblooms.domain.wishlist.likes.LikedStoreInfoResponse toCommonDto() {
        return bloomingblooms.domain.wishlist.likes.LikedStoreInfoResponse.builder()
                .storeId(storeId)
                .storeName(storeName)
                .detailInfo(detailInfo)
                .averageRating(averageRating)
                .build();
    }
}
