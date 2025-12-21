package com.justintime.jit.service.impl;

import com.justintime.jit.bean.JwtBean;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.security.CustomAuthToken;
import com.justintime.jit.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;
    private final UserRepository userRepository;
    private static final long accessTokenExpiration = 1000 * 60 * 60;
    private static final long refreshTokenExpiration = 1000 * 60 * 60 * 12;

    @Autowired
    public JwtServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public BeanFactory beanFactory;

    private List<String> getRestaurantCodes(String email) {
        return userRepository.findRestaurantCodesByEmail(email);
    }

    private String generateToken(String email, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        List<String> restaurantCodes = getRestaurantCodes(email);
        claims.put("role", userRepository.findByEmail(email).getRole().toString());
        if(!restaurantCodes.isEmpty()) {
            if(restaurantCodes.size() == 1) {
                claims.put("restaurantCode", restaurantCodes.get(0));
            } else {
                claims.put("restaurantCodes", restaurantCodes);
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

    @Override
    public Object extractRestaurantCode(String token) {
        return extractClaim(token, claims -> {
            if (claims.containsKey("restaurantCode")) {
                return claims.get("restaurantCode", String.class);
            } else if (claims.containsKey("restaurantCodes")) {
                return claims.get("restaurantCodes", List.class);
            }
            return null;
        });
    }

    @Override
    public void createJwtBean(Authentication authentication, String token) {
        JwtBean jwtBean = beanFactory.getBean(JwtBean.class);
        jwtBean.setUsername(authentication.getName());
        jwtBean.setRoles(authentication.getAuthorities());
        jwtBean.setToken(token);
        if (authentication instanceof CustomAuthToken customAuthToken) {
            jwtBean.setRestaurantCode(customAuthToken.getRestaurantCode());
        }
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
