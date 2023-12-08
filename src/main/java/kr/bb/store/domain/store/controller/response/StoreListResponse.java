package kr.bb.store.domain.store.controller.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreListResponse {
    private Long storeId;
    private String storeThumbnailImage;
    private String storeName;
    private String detailInfo;
    private Double averageRating;
    private Boolean isLiked;
    private String address;
    private String detailAddress;

    @QueryProjection
    public StoreListResponse(Long storeId, String storeThumbnailImage, String storeName, String detailInfo, Double averageRating, String address, String detailAddress) {
        this.storeId = storeId;
        this.storeThumbnailImage = storeThumbnailImage;
        this.storeName = storeName;
        this.detailInfo = detailInfo;
        this.averageRating = averageRating;
        this.address = address;
        this.detailAddress = detailAddress;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }
}
