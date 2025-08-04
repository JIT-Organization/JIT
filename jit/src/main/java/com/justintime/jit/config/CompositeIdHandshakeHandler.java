package com.justintime.jit.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.Map;

public class CompositeIdHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest req,
                                      WebSocketHandler wsHandler,
                                      Map<String,Object> attributes) {
        MultiValueMap<String,String> qp = UriComponentsBuilder.fromUri(req.getURI()).build().getQueryParams();
        String encodedUser = qp.getFirst("user");
        if (encodedUser == null || encodedUser.isEmpty()) {
            throw new IllegalArgumentException("Missing 'user' query parameter");
        }
        byte[] decodedBytes = Base64.getDecoder().decode(encodedUser);
        String key = new String(decodedBytes, StandardCharsets.UTF_8);
        return () -> key;
    }
}
