package com.justintime.jit.entity;

import com.justintime.jit.entity.Enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String email;
    private final String passwordHash;
    private final Role role;

    public UserPrincipal(User user) {
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
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
        return true; // you can customize this based on your User entity
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // return false if you track locked accounts
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // return false if credentials expire
    }

    @Override
    public boolean isEnabled() {
        return true; // return false if user is disabled
    }
}
