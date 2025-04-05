package com.justintime.jit.entity.Enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    COOK,
    SERVER,
    CUSTOMER;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
