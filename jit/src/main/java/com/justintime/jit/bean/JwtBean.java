package com.justintime.jit.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Collection;

@Component
@NoArgsConstructor
@Getter
@Setter
@RequestScope
public class JwtBean {
    String username;
    String restaurantCode;
    Collection<? extends GrantedAuthority> roles;
    String token;
}
