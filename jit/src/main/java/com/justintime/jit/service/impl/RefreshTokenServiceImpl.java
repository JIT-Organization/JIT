package com.justintime.jit.service.impl;

import com.justintime.jit.service.JwtService;
import com.justintime.jit.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();
    private final JwtService jwtService;

    @Autowired
    public RefreshTokenServiceImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String createRefreshToken(String email) {
        String refreshToken = jwtService.generateRefreshToken(email);
        refreshTokenStore.put(refreshToken, email);
        return refreshToken;
    }

    @Override
    public boolean validateRefreshToken(String token) {
        if (!refreshTokenStore.containsKey(token)) {
            return false;
        }
        try {
            jwtService.extractEmail(token);
            return true;
        } catch (Exception e) {
            refreshTokenStore.remove(token);
            return false;
        }
    }

    @Override
    public String rotateRefreshToken(String oldToken) {
        if (!validateRefreshToken(oldToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }
        String email = refreshTokenStore.get(oldToken);
        refreshTokenStore.remove(oldToken);
        return createRefreshToken(email);
    }

    @Override
    public String getEmailFromRefreshToken(String token) {
        return jwtService.extractEmail(token);
    }
}
