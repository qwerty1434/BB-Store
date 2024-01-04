package kr.bb.store.domain.coupon.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRequest {
    private String nickname;
    private String phoneNumber;
}
