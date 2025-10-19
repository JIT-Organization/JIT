package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.SubscriptionRequest;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jit-api/notifications")
public class NotificationController extends BaseController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<String>> subscribeForPushNotifications(@RequestBody SubscriptionRequest subscriptionRequest) {
        notificationService.subscribePushNotifications(subscriptionRequest);
        return success(null, "User Subscribed");
    }
}
