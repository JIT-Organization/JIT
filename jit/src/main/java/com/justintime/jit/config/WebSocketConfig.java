package com.justintime.jit.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ThreadPoolTaskScheduler brokerScheduler;

    @SuppressFBWarnings(value = "EI2", justification = "Scheduler is effectively immutable, safe to store reference")
    public WebSocketConfig(@Qualifier("wsBrokerScheduler") ThreadPoolTaskScheduler brokerScheduler) {
        this.brokerScheduler = brokerScheduler;
    }

    /* ---------- Broker topology ---------- */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
                .setApplicationDestinationPrefixes("/jit-api")
                .setUserDestinationPrefix("/user")
                .enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{10_000L, 10_000L})
                .setTaskScheduler(brokerScheduler);
    }

    /* ---------- WebSocket endpoints ---------- */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://localhost:8080",
                        "https://ui.graystone-894984be.centralindia.azurecontainerapps.io/", "https://app.jit-apps.com");
    }

    /* ---------- AuthorizationManager bean (rules ordered first-match) ---------- */
    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {

        messages
                .nullDestMatcher().authenticated()  // CONNECT requires authentication (from JWT)
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.DISCONNECT,
                        SimpMessageType.SUBSCRIBE, SimpMessageType.UNSUBSCRIBE).permitAll()
                .simpDestMatchers("/user/**", "/topic/**", "/queue/**").authenticated()
                .simpDestMatchers("/jit-api/**").authenticated()
                .anyMessage().denyAll();  // Deny unmatched

        return messages.build();
    }
}
