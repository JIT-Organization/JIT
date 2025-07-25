package com.justintime.jit.service;

public interface PushService {
    void broadcastToRole(String restaurant, String role, String event, Object payload);
    void sendToUser(String restaurant, String email, String role, String event, Object payload);
}
