package kr.bb.store.client.dto;

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
public class StoreNameAndAddressDto {
    private String storeName;
    private String storeAddress;

    public static StoreNameAndAddressDto of(Store store, StoreAddress storeAddress) {
        return StoreNameAndAddressDto.builder()
                .storeName(store.getStoreName())
                .storeAddress(storeAddress.getAddress() + " " +storeAddress.getDetailAddress())
                .build();
    }

    public bloomingblooms.domain.store.StoreNameAndAddressDto toCommonEntity() {
        return bloomingblooms.domain.store.StoreNameAndAddressDto.builder()
                .storeName(storeName)
                .storeAddress(storeAddress)
                .build();
    }
}
