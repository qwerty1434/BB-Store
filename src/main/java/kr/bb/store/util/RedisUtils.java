package kr.bb.store.util;

import kr.bb.store.domain.coupon.entity.Coupon;

public class RedisUtils {
    public static final String DUMMY_DATA = "DUMMY";

    public static String makeRedisKey(Coupon coupon) {
        return "coupon:" + coupon.getCouponCode() + ":" + coupon.getId();
    }

    public static String makeRedissonKey(Long storeId, Long flowerId) {
        return  "redisson:" + storeId + ":" + flowerId;
    }

    public static String makeRedissonKey(Long storeId) {
        return "redisson:" + storeId.toString();
    }
}
