package com.justintime.jit.entity.Enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role implements GrantedAuthority {
    ADMIN(4),
    SERVER(3),
    COOK(2),
    CUSTOMER(1);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }

    public static Role fromValue(int value) {
        for (Role role : Role.values()) {
            if (role.value == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role value: " + value);
    }
}
