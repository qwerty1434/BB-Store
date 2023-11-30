package kr.bb.store.domain.store.controller.response;

import com.querydsl.core.annotations.QueryProjection;
import kr.bb.store.domain.store.dto.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreForMapResponse {
    private Long storeId;
    private String storeName;
    private Boolean isLiked;
    private String detailInfo;
    private String thumbnailImage;
    private Double averageRating;
    private Position position;

    @QueryProjection
    public StoreForMapResponse(Long storeId, String storeName,String detailInfo, String thumbnailImage, Double averageRating, Double lat, Double lon) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.detailInfo = detailInfo;
        this.thumbnailImage = thumbnailImage;
        this.averageRating = averageRating;
        this.position = new Position(lat,lon);
    }
}
