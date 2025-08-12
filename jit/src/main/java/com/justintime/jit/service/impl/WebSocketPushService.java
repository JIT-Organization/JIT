package com.justintime.jit.service.impl;

import com.justintime.jit.service.PushService;
import com.justintime.jit.util.LoggingAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public void sendToUser(String email, String event, Object payload) {
        template.convertAndSendToUser(email, "/queue/"+event, payload);
    }
}
