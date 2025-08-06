package com.justintime.jit.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomAuthToken extends UsernamePasswordAuthenticationToken {
    private final String restaurantCode;
    public CustomAuthToken(String restaurantCode, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.restaurantCode = restaurantCode;
    }
}
