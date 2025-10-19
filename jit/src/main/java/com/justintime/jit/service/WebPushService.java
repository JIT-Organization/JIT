package com.justintime.jit.service;

import com.justintime.jit.entity.PushSubscription;

public interface WebPushService {
    void sendNotification(PushSubscription subscription, String payloadJson);
}
