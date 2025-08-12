package com.justintime.jit.service;

public interface PushService {
    void broadcastToRole(String restaurant, String role, String event, Object payload);
    void sendToUser(String email, String event, Object payload);
}
