package kr.bb.store.client.dto;

import com.querydsl.core.annotations.QueryProjection;
import kr.bb.store.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreInfoDto {
    private Long storeId;
    private String storeCode;
    private String storeName;
    private String detailInfo;
    private String storeThumbnailInfo;
    private Double averageRating;
    private String phoneNumber;
    private String accountNumber;
    private String bank;

    public static StoreInfoDto fromEntity(Store store) {
        return StoreInfoDto.builder()
                .storeId(store.getId())
                .storeCode(store.getStoreCode())
                .storeName(store.getStoreName())
                .detailInfo(store.getDetailInfo())
                .storeThumbnailInfo(store.getStoreThumbnailImage())
                .averageRating(store.getAverageRating())
                .phoneNumber(store.getPhoneNumber())
                .accountNumber(store.getAccountNumber())
                .bank(store.getBank())
                .build();
    }

    public bloomingblooms.domain.store.StoreInfoDto toCommonEntity() {
        return bloomingblooms.domain.store.StoreInfoDto.builder()
                .storeId(storeId)
                .storeCode(storeCode)
                .storeName(storeName)
                .detailInfo(detailInfo)
                .storeThumbnailInfo(storeThumbnailInfo)
                .averageRating(averageRating)
                .phoneNumber(phoneNumber)
                .accountNumber(accountNumber)
                .bank(bank)
                .build();
    }
}
