package com.justintime.jit.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String email;
    private final String passwordHash;
    private final RestaurantRole restaurantRole;

    public UserPrincipal(User user) {
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.restaurantRole = user.getRestaurantRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (restaurantRole != null) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + restaurantRole.getName()));
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
