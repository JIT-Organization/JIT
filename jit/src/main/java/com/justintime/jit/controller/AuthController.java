package com.justintime.jit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.LoginRequestDto;
import com.justintime.jit.dto.UserDTO;
import com.justintime.jit.service.UserAuthService;
import com.justintime.jit.util.LoggingAspect;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/")
public class AuthController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("register")
    public void register(@Nullable @RequestParam String token, @RequestBody UserDTO user) {
        userAuthService.register(token, user);
    }

    @PostMapping("login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) throws LoginException {
        userAuthService.login(loginRequestDto, response);
        return success(null, "Login Successful");
    }

    @PostMapping("refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        userAuthService.refresh(request, response);
        return success(null);
    }

    @GetMapping("/test-logs")
    public void test() {
        for(int i=0; i<50; i++) {
            logger.info("Test message");
        }
    }
}
