package kr.bb.store.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreAddressDto {
    private String sido;
    private String gugun;
    private String address;
    private String detailAddress;
    private String zipCode;
    private Double lat;
    private Double lon;
}
