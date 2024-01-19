package kr.bb.store.util;

import kr.bb.store.domain.coupon.entity.Coupon;

public class RedisUtils {
    public static final String DUMMY_DATA = "DUMMY";

    public static String makeRedisKey(String couponCode, String couponId) {
        return "coupon:" + couponCode + ":" + couponId;
    }

    public static String makeRedissonKey(Long storeId, Long flowerId) {
        return  "redisson:" + storeId + ":" + flowerId;
    }

    public static String makeRedissonKey(Long storeId) {
        return "redisson:" + storeId.toString();
    }
}
