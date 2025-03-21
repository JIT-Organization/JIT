package com.justintime.jit.service.impl;

import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private static final String secretKey = "N3VzdCFuVCFtMzUzY3JldEtleTRKU09Od2ViVDBrM25z";
    private final UserRepository userRepository;
    private static final long accessTokenExpiration = 1000 * 60 * 15;
    private static final long refreshTokenExpiration = 1000 * 60 * 60 * 12;

    @Autowired
    public JwtServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private List<Long> getRestaurantIds(String email) {
        return userRepository.findRestaurantIdsByEmail(email);
    }

    private String generateToken(String email, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        List<Long> restaurantIds = getRestaurantIds(email);
        claims.put("role", userRepository.findByEmail(email).getRole().toString());
        if(!restaurantIds.isEmpty()) {
            if(restaurantIds.size() == 1) {
                claims.put("restaurantId", restaurantIds.get(0));
            } else {
                claims.put("restaurantIds", restaurantIds);
            }
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(String email) {
        return generateToken(email, refreshTokenExpiration);
    }

    @Override
    public String generateAccessToken(String email) {
        return generateToken(email, accessTokenExpiration);
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
