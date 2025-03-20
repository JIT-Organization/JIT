package com.justintime.jit.service;

public interface RefreshTokenService {
    String createRefreshToken(String email);
    boolean validateRefreshToken(String token);
    String rotateRefreshToken(String oldToken);
    String getEmailFromRefreshToken(String token);
}
