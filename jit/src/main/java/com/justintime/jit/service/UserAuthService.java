package com.justintime.jit.service;

import com.justintime.jit.dto.LoginRequestDto;
import com.justintime.jit.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.security.auth.login.LoginException;

public interface UserAuthService {
    void register(User user);
    String login(LoginRequestDto loginRequestDto, HttpServletResponse response) throws LoginException;
    String refresh(HttpServletRequest request, HttpServletResponse response);
}
