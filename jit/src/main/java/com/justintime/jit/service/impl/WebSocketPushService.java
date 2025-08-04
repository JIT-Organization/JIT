package com.justintime.jit.service.impl;

import com.justintime.jit.service.PushService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketPushService implements PushService {
    private final SimpMessagingTemplate template;

    public WebSocketPushService(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void broadcastToRole(String restaurantCode, String role, String eventName, Object body) {
        String destination = "/topic/%s/%s/%s".formatted(restaurantCode, role, eventName);
        template.convertAndSend(destination, body);
    }

    @Override
    public void sendToUser(String restaurantCode, String email, String role, String event, Object payload) {
        String userId = "%s|%s|%s".formatted(restaurantCode, email, role);
        template.convertAndSendToUser(userId, "/queue/"+event, payload);
    }
}
