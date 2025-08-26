package com.justintime.jit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justintime.jit.dto.LoginRequestDto;
import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.dto.UserDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.entity.User;
import com.justintime.jit.entity.UserInvitationToken;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.exception.TokenExpiredException;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.repository.UserInvitationRepository;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.JwtService;
import com.justintime.jit.service.PermissionsService;
import com.justintime.jit.service.RefreshTokenService;
import com.justintime.jit.service.UserAuthService;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.security.auth.login.LoginException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserAuthServiceImpl extends BaseServiceImpl<User, Long> implements UserAuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final PermissionsService permissionsService;

    private final GenericMapper<User, UserDTO> userMapper = MapperFactory.getMapper(User.class, UserDTO.class);

    private final RestaurantRepository restaurantRepository;

    private final UserInvitationRepository userInvitationRepository;

    @SuppressFBWarnings(value = "EI2", justification = "User Auth Service Impl is a Spring-managed bean and is not exposed.")
    public UserAuthServiceImpl(PasswordEncoder passwordEncoder, JwtService jwtService,
                               RefreshTokenService refreshTokenService,
                               AuthenticationManager authenticationManager, UserRepository userRepository, PermissionsService permissionsService, RestaurantRepository restaurantRepository, UserInvitationRepository userInvitationRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.permissionsService = permissionsService;
        this.restaurantRepository = restaurantRepository;
        this.userInvitationRepository = userInvitationRepository;
    }

    @Override
    @Transactional
    public void register(@Nullable String token, UserDTO userDTO) {
        User user;
        if(StringUtils.equalsIgnoreCase(token, "null")) token = null;
        if(StringUtils.isNotEmpty(token)) {
            UserInvitationToken userInvitationToken = userInvitationRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Token"));

            if(userInvitationToken.isUsed() || LocalDateTime.now().isAfter(userInvitationToken.getExpiresAt())) {
                throw new TokenExpiredException("Token expired or already used");
            }

            user = userInvitationToken.getUser();
            user.setIsActive(true);

            userInvitationToken.setUsed(true);
            userInvitationRepository.save(userInvitationToken);
        } else {
            user = userMapper.toEntity(userDTO);
            user.setIsActive(true);
        }
        enrichUserDetails(user, userDTO);
        userRepository.save(user);
    }

    @Override
    public void login(LoginRequestDto loginRequestDto, HttpServletResponse response) throws LoginException {
        try{
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
            if(authentication.isAuthenticated()){
                ObjectMapper objectMapper = new ObjectMapper();
                String accessToken = jwtService.generateAccessToken(loginRequestDto.getEmail());
                String refreshToken = refreshTokenService.createRefreshToken(loginRequestDto.getEmail());
                List<String> restaurantCodesList = userRepository.findRestaurantCodesByEmail(loginRequestDto.getEmail());
                List<PermissionsDTO> permissionsDTOList = permissionsService.getAllPermissionsByUserEmail(authentication.getName());
                String restaurantCodes = Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(restaurantCodesList).getBytes(StandardCharsets.UTF_8));
                String permissions = Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(permissionsDTOList.stream().map(PermissionsDTO::getPermissionCode).collect(Collectors.toSet())).getBytes(StandardCharsets.UTF_8));
                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
//                        .secure(true)
                        .path("/")
                        .maxAge(12 * 60 * 60)
                        .sameSite("Strict")
                        .build();
                ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                        .httpOnly(true)
//                        .secure(true)
                        .path("/")
                        .maxAge(15 * 60)
                        .sameSite("Strict")
                        .build();
                ResponseCookie resCodesCookie = ResponseCookie.from("resCodes", restaurantCodes)
                        .path("/")
//                        .secure(true)
                        .sameSite("Strict")
                        .build();
                ResponseCookie permissionsCookie = ResponseCookie.from("permissions", permissions)
                        .path("/")
//                        .secure(true)
                        .sameSite("Strict")
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
                response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
                response.addHeader(HttpHeaders.SET_COOKIE, resCodesCookie.toString());
                response.addHeader(HttpHeaders.SET_COOKIE, permissionsCookie.toString());
//                return accessToken;
            }
        } catch (Exception e) {
            throw new LoginException("Try Again " + e.getMessage());
        }
    }

    @Override
    public void refresh(HttpServletRequest request, HttpServletResponse response) throws TokenExpiredException, JsonProcessingException {
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

        ObjectMapper objectMapper = new ObjectMapper();
        String newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);
        String email = refreshTokenService.getEmailFromRefreshToken(newRefreshToken);
        String newAccessToken = jwtService.generateAccessToken(email);
        List<String> restaurantCodesList = userRepository.findRestaurantCodesByEmail(email);
        String restaurantCodes = Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(restaurantCodesList).getBytes(StandardCharsets.UTF_8));
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
//                .secure(true)
                .path("/")
                .maxAge(12* 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
//                        .secure(true)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Strict")
                .build();
        ResponseCookie resCodesCookie = ResponseCookie.from("resCodes", restaurantCodes)
                .path("/")
//                        .secure(true)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, resCodesCookie.toString());
//        return newAccessToken;
    }

    private void enrichUserDetails(User user, UserDTO userDTO) {
        String password = userDTO.getPassword();
        Set<String> permissionCodes = userDTO.getPermissionCodes();
        if(StringUtils.isNotBlank(password)) user.setPasswordHash(passwordEncoder.encode(password));
        if(Objects.nonNull(userDTO.getRole())) user.setRole(Role.valueOf(userDTO.getRole()));
        if(!CollectionUtils.isEmpty(permissionCodes)) user.setPermissions(permissionsService.getAllPermissionsByPermissionCodes(permissionCodes));
        if(Objects.nonNull(userDTO.getRestaurantCodes())) {
            Set<Restaurant> restaurants = new HashSet<>(restaurantRepository.findByRestaurantCodeIn(userDTO.getRestaurantCodes())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant(s) not found, please contact admin")));
            if (restaurants.size() != userDTO.getRestaurantCodes().size()) {
                throw new ResourceNotFoundException("Some restaurant codes are missing, please contact admin.");
            }
            user.setRestaurants(restaurants);
        }
    }
}
