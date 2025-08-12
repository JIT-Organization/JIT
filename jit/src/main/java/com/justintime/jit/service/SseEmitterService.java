package com.justintime.jit.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterService {
    void sendEvent(String restaurantCode, String email, String role, String eventName, Object eventData);
    void removeEmitter(String key, SseEmitter emitter);
    SseEmitter register(String restaurantCode, String email, String role);
}
