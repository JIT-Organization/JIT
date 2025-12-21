package com.justintime.jit.service.impl;

import com.justintime.jit.service.SseEmitterService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service // Use this class for sending the received events
public class SseEmitterServiceImpl implements SseEmitterService {

    private final Map<String, List<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    // TODO Add heartbeat check
    /*
    TODO If we are going to send separate event for everyone then we need to come up with the below
        1. Write a code for getting the emitter based on the key for broadcasts like emitter starting with TGSR|COOK for all cooks in TGSR
        2. Use send event method for sending the data thru that emitters to the specified users alone
     */
    // TODO Add failed message check in InMemoryEmitterQueue and if req create a table for that
    @Override
    public void sendEvent(String restaurantCode, String email, String role, String eventName, Object eventData) {
        String key = buildEmitterKey(restaurantCode, email, role);
        List<SseEmitter> emitters = emittersMap.getOrDefault(key, List.of());
        for(SseEmitter emitter: emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(eventData).id(UUID.randomUUID().toString()));
            } catch (IOException e) {
                emitter.complete();
            }
        }
    }

    @Override
    public void removeEmitter(String key, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersMap.get(key);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }

    @Override
    public SseEmitter register(String restaurantCode, String email, String role) {
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        String key = buildEmitterKey(restaurantCode, email, role);
        emitter.onCompletion(() -> removeEmitter(key, emitter));
        emitter.onTimeout(() -> removeEmitter(key, emitter));
        emitter.onError(e -> removeEmitter(key, emitter));
        emittersMap.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(emitter);
        return emitter;
    }

    private String buildEmitterKey(String restaurantCode, String email, String role) {
        return restaurantCode + "|" + email + "|" + role;
    }
}
