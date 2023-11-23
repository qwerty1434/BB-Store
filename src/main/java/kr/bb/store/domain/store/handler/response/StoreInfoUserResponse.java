package kr.bb.store.domain.store.handler.response;

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
    private Double averageRating;
    private String detailInfo;
    private String phoneNumber;
    private Boolean isLiked;
    private Boolean isSubscribed;
}
