package com.justintime.jit.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.lang.reflect.Field;
import java.util.List;

@Configuration
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthorizationManager<Message<?>> authorizationManager;
    private final ApplicationContext context;

    public WebSocketSecurityConfig(
            AuthorizationManager<Message<?>> authorizationManager,
            ApplicationContext context) {
        this.authorizationManager = authorizationManager;
        this.context = context;
    }

    /* expose @AuthenticationPrincipal in @MessageMapping methods */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    /* attach Security + Authorization interceptors to inbound channel (omit CSRF to disable it) */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        try {
            Field interceptorsField = ChannelRegistration.class.getDeclaredField("interceptors");
            interceptorsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<ChannelInterceptor> interceptors = (List<ChannelInterceptor>) interceptorsField.get(registration);

            // Remove the XorCsrfChannelInterceptor if it exists
            interceptors.removeIf(interceptor -> interceptor.getClass().getSimpleName().equals("XorCsrfChannelInterceptor"));

            // Now add your custom interceptors
            AuthorizationChannelInterceptor authz = new AuthorizationChannelInterceptor(authorizationManager);
            AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(context);
            authz.setAuthorizationEventPublisher(publisher);

            interceptors.add(new SecurityContextChannelInterceptor());
            interceptors.add(new LoggingChannelInterceptor());
            interceptors.add(authz);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to override interceptors", e);
        }
    }
}
