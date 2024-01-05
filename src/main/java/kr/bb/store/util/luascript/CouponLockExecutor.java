package kr.bb.store.util.luascript;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class CouponLockExecutor implements RedisLuaScriptExecutor{

    private final RedisTemplate<String,String> redisTemplate;

    @Override
    public Boolean execute(String script, String key, Object... args) {
        RedisScript<Boolean> redisScript = new DefaultRedisScript<>(script, Boolean.class);
        return redisTemplate.execute(redisScript, Collections.singletonList(key), args[0], String.valueOf(args[1]));
    }

}
