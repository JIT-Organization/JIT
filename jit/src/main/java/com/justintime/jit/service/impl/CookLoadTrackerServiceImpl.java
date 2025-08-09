package com.justintime.jit.service.impl;

import com.justintime.jit.dto.CookLoadDeltaDTO;
import com.justintime.jit.service.CookLoadTrackerService;
import com.justintime.jit.util.CookLoadDeltaWriter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CookLoadTrackerServiceImpl implements CookLoadTrackerService {
    private final StringRedisTemplate redis;
    private final CookLoadDeltaWriter deltaWriter; // async writer
    private final ConcurrentHashMap<Long, Integer> localFallback = new ConcurrentHashMap<>();
    private final boolean useRedis;
    private final Duration snapshotTtl = Duration.ofMinutes(60);

    public CookLoadTrackerServiceImpl(StringRedisTemplate redis, CookLoadDeltaWriter deltaWriter) {
        this.redis = redis;
        this.deltaWriter = deltaWriter;
        this.useRedis = redis != null;
    }

    private String redisKey(Long cookId) {
        return "cook:load:" + cookId;
    }

    @Override
    public int getCookLoad(long cookId) {
        if (useRedis) {
            String v = redis.opsForValue().get(redisKey(cookId));
            return Integer.parseInt(v);
        }
        return localFallback.getOrDefault(cookId, 0);
    }

    @Override
    public void addLoad(long cookId, int minutes, long orderItemId) {
        if (minutes == 0) return;
        if (useRedis) {
            redis.opsForValue().increment(redisKey(cookId), minutes);
            redis.expire(redisKey(cookId), snapshotTtl);
        } else {
            localFallback.merge(cookId, minutes, Integer::sum);
        }
        deltaWriter.enqueue(CookLoadDeltaDTO.builder().deltaMinutes(minutes).cookId(cookId).orderItemId(orderItemId).processed(false).build());
    }

    @Override
    public void removeLoad(long cookId, int minutes, long orderItemId) {
        if (minutes == 0) return;
        if (useRedis) {
            redis.opsForValue().increment(redisKey(cookId), -minutes);
            redis.expire(redisKey(cookId), snapshotTtl);
        } else {
            localFallback.computeIfPresent(cookId, (id, load) -> Math.max(0, load - minutes));
        }
        deltaWriter.enqueue(CookLoadDeltaDTO.builder().deltaMinutes(-minutes).cookId(cookId).orderItemId(orderItemId).processed(true).build());
    }
}
