package kr.bb.store.domain.store.controller.response;

import kr.bb.store.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleStoreResponse {
    private Long storeId;
    private String storeThumbnailImage;
    private String storeName;
    private String detailInfo;
    private Double averageRating;

    public static SimpleStoreResponse from(Store store) {
        return SimpleStoreResponse.builder()
                .storeId(store.getId())
                .storeThumbnailImage(store.getStoreThumbnailImage())
                .storeName(store.getStoreName())
                .detailInfo(store.getDetailInfo())
                .averageRating(store.getAverageRating())
                .build();
    }
}
