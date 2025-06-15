package com.justintime.jit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.justintime.jit.dto.LoginRequestDto;
import com.justintime.jit.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.security.auth.login.LoginException;

public interface UserAuthService {
    void register(User user);
    void login(LoginRequestDto loginRequestDto, HttpServletResponse response) throws LoginException;
    void refresh(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException;
}
