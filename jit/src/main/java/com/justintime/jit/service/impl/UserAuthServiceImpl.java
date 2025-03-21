package com.justintime.jit.service.impl;

import com.justintime.jit.dto.LoginRequestDto;
import com.justintime.jit.entity.User;
import com.justintime.jit.exception.TokenExpiredException;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.JwtService;
import com.justintime.jit.service.RefreshTokenService;
import com.justintime.jit.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
public class UserAuthServiceImpl extends BaseServiceImpl<User, Long> implements UserAuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    @Autowired
    public UserAuthServiceImpl(PasswordEncoder passwordEncoder,JwtService jwtService,
                               RefreshTokenService refreshTokenService,
                               AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void register(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userRepository.save(user);
    }

    @Override
    public String login(LoginRequestDto loginRequestDto, HttpServletResponse response) throws LoginException {
        try{
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
            if(authentication.isAuthenticated()){
                String accessToken = jwtService.generateAccessToken(loginRequestDto.getEmail());
                String refreshToken = refreshTokenService.createRefreshToken(loginRequestDto.getEmail());
                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
//                        .secure(true)
                        .path("/refresh")
                        .maxAge(12 * 60 * 60)
                        .sameSite("Strict")
                        .build();
                response.addHeader("Set-Cookie", refreshCookie.toString());
                return accessToken;
            }
        } catch (Exception e) {
            throw new LoginException("Try Again");
        }
        return "";
    }

    @Override
    public String refresh(HttpServletRequest request, HttpServletResponse response) throws TokenExpiredException {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || !refreshTokenService.validateRefreshToken(refreshToken)) {
            throw new TokenExpiredException("Please login again");
        }

        String newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);
        String email = refreshTokenService.getEmailFromRefreshToken(newRefreshToken);
        String newAccessToken = jwtService.generateAccessToken(email);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
//                .secure(true)
                .path("/refresh")
                .maxAge(12* 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return newAccessToken;
    }
}
