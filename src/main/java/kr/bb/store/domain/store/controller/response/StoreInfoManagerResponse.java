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
public class StoreInfoManagerResponse {
    private String storeName;
    private String storeThumbnailImage;
    private String phoneNumber;
    private String accountNumber;
    private String bank;
    private String detailInfo;
    private String address;
    private String addressDetail;

    public static StoreInfoManagerResponse of(Store store, StoreAddress storeAddress) {
        return StoreInfoManagerResponse.builder()
                .storeName(store.getStoreName())
                .storeThumbnailImage(store.getStoreThumbnailImage())
                .phoneNumber(store.getPhoneNumber())
                .accountNumber(store.getAccountNumber())
                .bank(store.getBank())
                .detailInfo(store.getDetailInfo())
                .address(storeAddress.getAddress())
                .addressDetail(storeAddress.getDetailAddress())
                .build();
    }
}
