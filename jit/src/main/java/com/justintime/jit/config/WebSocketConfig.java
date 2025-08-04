package com.justintime.jit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
//@EnableRetry // TODO already present in another pr once merged uncomment this line
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private ThreadConfig threadConfig;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/jit-api")
                .setUserDestinationPrefix("/user")
//                 Enable if Message Queues are used
//                .enableStompBrokerRelay("/topic", "queue")
//                .setRelayHost("${relay.host}")
//                .setRelayPort(61613)
//                .setSystemLogin("${relay.system.login}")
//                .setSystemPasscode("${relay.system.passcode}")
//                .setClientLogin("${relay.client.login}")
//                .setClientPasscode("${relay.client.passcode}")
//                .setSystemHeartbeatSendInterval(10_000)
//                .setSystemHeartbeatReceiveInterval(10_000)
                .enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{10000L, 10000L})
                .setTaskScheduler(threadConfig.messageBrokerTaskScheduler());
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new CompositeIdHandshakeHandler())
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
