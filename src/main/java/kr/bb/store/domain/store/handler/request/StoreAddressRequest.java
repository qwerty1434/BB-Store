package kr.bb.store.domain.store.handler.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreAddressRequest {
    private String sido;
    private String gugun;
    private String address;
    private String detailAddress;
    private String zipCode;
    private Float lat;
    private Float lon;
}
