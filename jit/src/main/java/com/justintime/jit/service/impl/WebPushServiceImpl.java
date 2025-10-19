package com.justintime.jit.service.impl;

import com.justintime.jit.entity.PushSubscription;
import com.justintime.jit.repository.PushSubscriptionRepository;
import com.justintime.jit.service.WebPushService;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.concurrent.ExecutionException;

@Service
public class WebPushServiceImpl implements WebPushService {
    private static final Logger logger = LoggerFactory.getLogger(WebPushService.class);

    @Value("${vapid.public.key}")
    private String publicKey;
    @Value("${vapid.private.key}")
    private String privateKey;
    @Value("${vapid.subject}")
    private String subject;

    private PushService pushService;
    private final PushSubscriptionRepository subscriptionRepository;

    public WebPushServiceImpl(PushSubscriptionRepository subscriptionRepository) {
        Security.addProvider(new BouncyCastleProvider());
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostConstruct
    private void init() throws GeneralSecurityException {
        this.pushService = new PushService(publicKey, privateKey, subject);
    }

//    @Async
    public void sendNotification(PushSubscription sub, String payloadJson) {
        Subscription target = new Subscription(sub.getEndpoint(), new Subscription.Keys(sub.getP256dh(), sub.getAuth()));
        try {
            HttpResponse response = pushService.send(new Notification(target, payloadJson));
            logger.info("Push sent: {} {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        } catch (GeneralSecurityException | IOException | JoseException | ExecutionException | InterruptedException e) {
            logger.error("Error sending push notification: {}", e.getMessage());

            // This is critical: if a subscription is expired or invalid, the push service
            // returns an error (often 404 or 410). We must delete it from our DB.
            if (e instanceof ExecutionException) {
                String message = e.getMessage();
                if (message.contains("404") || message.contains("410")) {
                    logger.info("Deleting expired/invalid subscription with ID: {}", sub.getId());
                    subscriptionRepository.delete(sub);
                }
            }
        }
    }
}
