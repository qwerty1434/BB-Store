package kr.bb.store.domain.coupon.util;

import kr.bb.store.domain.coupon.entity.Coupon;

public class RedisUtils {
    public static final String DUMMY_DATA = "DUMMY";

    public static String makeRedisKey(Coupon coupon) {
        return "coupon:" + coupon.getCouponCode() + ":" + coupon.getId();
    }
}
