package com.authservice.application.provider;

import com.authservice.application.exception.RedisDataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisProvider implements InitializingBean {

    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> ops;

    @Override
    public void afterPropertiesSet() throws Exception {
        ops = redisTemplate.opsForValue();
    }

    public void setData(String key, Object value, long timeMillis) {
        ops.set(key, value, Duration.ofMillis(timeMillis));
    }

    public Object getData(String key) {
        return Optional.ofNullable(ops.get(key))
                .orElseThrow(RedisDataNotFoundException::new);
    }

    public void updateData(String key, Object newValue) {
        ops.getAndSet(key, newValue);
    }

    public void deleteData(String key) {
        ops.getAndDelete(key);
    }
}