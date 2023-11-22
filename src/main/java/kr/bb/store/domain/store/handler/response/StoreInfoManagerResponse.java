package kr.bb.store.domain.store.handler.response;

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
}
