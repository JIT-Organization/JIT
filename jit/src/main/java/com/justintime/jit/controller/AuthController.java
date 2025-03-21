package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.LoginRequestDto;
import com.justintime.jit.entity.User;
import com.justintime.jit.service.RefreshTokenService;
import com.justintime.jit.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/")
public class AuthController extends BaseController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("register")
    public void register(@RequestBody User user) {
        userAuthService.register(user);
    }

    @PostMapping("login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) throws LoginException {
        String token =  userAuthService.login(loginRequestDto, response);
        return success(token, "Login Successful");
    }

    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String newToken = userAuthService.refresh(request, response);
        return ResponseEntity.ok(newToken);
    }
}
