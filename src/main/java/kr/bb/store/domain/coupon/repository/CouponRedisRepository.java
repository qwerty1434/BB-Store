package kr.bb.store.domain.coupon.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void setCount(String key) {
        redisTemplate.opsForValue().set(key,"0");
    }

    public void setExpirationDate(String key, LocalDate expirationDate) {
        redisTemplate.expireAt(key, Date.valueOf(expirationDate));
    }

    public Long increaseCount(String key) {
        Long increment = redisTemplate.opsForValue().increment(key);
        return increment;
    }

    public Long decreaseCount(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

}
