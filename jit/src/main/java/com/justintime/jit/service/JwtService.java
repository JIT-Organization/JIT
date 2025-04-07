package com.justintime.jit.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateRefreshToken(String email);
    String generateAccessToken(String email);
    String extractEmail(String token);
    boolean validateToken(String token, UserDetails userDetails);
    Object extractRestaurantId(String token);
}
