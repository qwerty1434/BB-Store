package kr.bb.store.util;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RedisOperation {
    private final RedisTemplate<String, String> redisTemplate;

    public void add(String key, String value) {
        redisTemplate.opsForSet().add(key,value);
    }

    public void remove(String key, String value) {
        redisTemplate.opsForSet().remove(key,value);
    }

    public void setExpr(String key, LocalDate expirationDate) {
        redisTemplate.expireAt(key, Date.valueOf(expirationDate));
    }

    public Boolean contains(String key, String value) {
        return redisTemplate.opsForSet().isMember(key,value);
    }

    public Long count(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public Object addAndSetExpr(String key, LocalDate expirationDate) {
        return redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                add(key, RedisUtils.DUMMY_DATA);
                setExpr(key,expirationDate);
                return operations.exec();
            }
        });
    }

    public Object countAndSet(String key, String value) {
        return redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                count(key);
                add(key,value);
                return operations.exec();
            }
        });
    }

}
