package com.justintime.jit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.justintime.jit.dto.LoginRequestDto;
import com.justintime.jit.dto.UserDTO;
import com.justintime.jit.entity.User;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.security.auth.login.LoginException;

public interface UserAuthService {
    void register(@Nullable String token, UserDTO user);
    void login(LoginRequestDto loginRequestDto, HttpServletResponse response) throws LoginException;
    void refresh(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException;
}
