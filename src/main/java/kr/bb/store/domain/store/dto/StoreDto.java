package kr.bb.store.domain.store.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    private String storeName;
    private String detailInfo;
    private String storeThumbnailImage;
    private String phoneNumber;
    private String accountNumber;
    private String bank;
}
