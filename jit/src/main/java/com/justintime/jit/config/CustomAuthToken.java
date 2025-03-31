package com.justintime.jit.config;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomAuthToken extends UsernamePasswordAuthenticationToken {
    private final Long restaurantId;
    public CustomAuthToken(Long restaurantId, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.restaurantId = restaurantId;
    }
}
