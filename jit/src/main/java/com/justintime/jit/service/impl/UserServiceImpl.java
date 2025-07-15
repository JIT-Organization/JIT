package com.justintime.jit.service.impl;

import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.dto.UserDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.event.UserInvitationEvent;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.repository.UserInvitationRepository;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.PermissionsService;
import com.justintime.jit.service.UserService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

    private final UserRepository userRepository;

    private final CommonServiceImplUtil commonServiceImplUtil;

    private final PermissionsService permissionsService;

    private final UserInvitationRepository userInvitationRepository;

    private final GenericMapper<User, UserDTO> userMapper = MapperFactory.getMapper(User.class, UserDTO.class);

    private final GenericMapper<Permissions, PermissionsDTO> permissionsMapper = MapperFactory.getMapper(Permissions.class, PermissionsDTO.class);

    private final RestaurantRepository restaurantRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Value("${register.invite.url}")
    private String registrationUrl;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CommonServiceImplUtil commonServiceImplUtil, PermissionsService permissionsService, UserInvitationRepository userInvitationRepository, RestaurantRepository restaurantRepository, RestaurantRepository restaurantRepository1) {
        this.userRepository = userRepository;
        this.commonServiceImplUtil = commonServiceImplUtil;
        this.permissionsService = permissionsService;
        this.userInvitationRepository = userInvitationRepository;
        this.restaurantRepository = restaurantRepository1;
    }

    @Override
    public List<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public void update(Long id, User updatedUser) {
        userRepository.findById(id).map(existingUser -> {
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
            existingUser.setIsActive(updatedUser.getIsActive());
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setPasswordHash(updatedUser.getPasswordHash());
            existingUser.setRole(updatedUser.getRole());
            existingUser.setUpdatedDttm(LocalDateTime.now()); // Set updated timestamp
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Override
    public List<UserDTO> getUsersByRestaurantCode(String restaurantCode) {
        List<User> users = userRepository.findAllByRestaurantCode(restaurantCode);
        return users.stream().map(user -> {
            Set<String> permissionsCodes = user.getPermissions().stream().map(Permissions::getPermissionCode).collect(Collectors.toSet());
            UserDTO userDTO = userMapper.toDto(user);
            userDTO.setPermissionCodes(permissionsCodes);
            return userDTO;
        }).toList();
    }

    @Override
    public UserDTO patchUpdateUser(String restaurantCode, String username, UserDTO dto, HashSet<String> propertiesToBeUpdated) {
        User existingUser = userRepository.findByRestaurantCodeAndUsername(restaurantCode, username);
        User patchedUser = userMapper.toEntity(dto);
        // TODO write a validation where the username should be unique if they are updating it
        if(propertiesToBeUpdated.contains("permissionCodes")) {
            Set<Permissions> permissions = permissionsService.getAllPermissionsByPermissionCodes(dto.getPermissionCodes());
            permissions.addAll(existingUser.getPermissions());
            patchedUser.setPermissions(permissions);
            propertiesToBeUpdated.remove("permissionCodes");
            propertiesToBeUpdated.add("permissions");
        }
        HashSet<String> propertiesToBeUpdatedClone = new HashSet<>(propertiesToBeUpdated);
        commonServiceImplUtil.copySelectedProperties(patchedUser, existingUser, propertiesToBeUpdatedClone);
        existingUser.setUpdatedDttm(LocalDateTime.now());
        userRepository.save(existingUser);
        Set<String> permissionCodes = existingUser.getPermissions().stream().map(Permissions::getPermissionCode).collect(Collectors.toSet());
        UserDTO savedUserDTO = userMapper.toDto(existingUser);
        savedUserDTO.setPermissionCodes(permissionCodes);
        return savedUserDTO;
    }

    @Override
    public UserDTO addOrUpdatePermissions(String email, List<PermissionsDTO> permissionsDTOS, boolean isEdit) throws AccessDeniedException {
        if(isEdit) {
            permissionsService.updatePermissions(email, false, permissionsDTOS);
        } else {
            Set<String> permissionCodes = permissionsDTOS.stream().map(PermissionsDTO::getPermissionCode).collect(Collectors.toSet());
            permissionsService.addPermissionsToUser(email, permissionCodes);
        }
        User user = userRepository.findByEmail(email);
        // TODO Add permissions DTO instead of permissions
        return userMapper.toDto(user);
    }

    @Override
    public List<PermissionsDTO> getAllPermissions() {
        return permissionsService.getAllPermissionsByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    @Transactional
    // TODO modify this method to a user registration link sender thru email service method
    public UserDTO addUser(UserDTO addUserRequest) {
        Set<String> permissionCodes = addUserRequest.getPermissionCodes();
        User user = userMapper.toEntity(addUserRequest);
        addPermissionsToUser(user, permissionCodes);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    // TODO get all possible details of the user in this itself, in link just pass the email so that the user can note that email and set the password alone
    @Override
    @Transactional
    public void sendInviteToUser(UserDTO inviteUserDTO) {
        User user = userMapper.toEntity(inviteUserDTO);
        user.setIsActive(false);
        Set<Restaurant> restaurants = restaurantRepository.findByRestaurantCodeIn(inviteUserDTO.getRestaurantCodes()).orElseThrow(() -> new ResourceNotFoundException("Restaurants Not Found for given codes"));
        user.setRestaurants(restaurants);
        userRepository.save(user);
        String token = UUID.randomUUID().toString();
        UserInvitationToken invitationToken = new UserInvitationToken();
        invitationToken.setToken(token);
        invitationToken.setEmail(user.getEmail());
        invitationToken.setExpiresAt(LocalDateTime.now().plusDays(1));
        invitationToken.setUser(user);
        userInvitationRepository.save(invitationToken);
        String restaurantName = "";
        if(Objects.nonNull(inviteUserDTO.getRestaurantCodes())) {
            if(restaurants.size() == 1) {
                restaurantName = restaurants.stream().findFirst().get().getRestaurantName();
            } else {
                if(inviteUserDTO.getRestaurantCodes().stream().findFirst().isPresent()) restaurantName = inviteUserDTO.getRestaurantCodes().stream().findFirst().get();
            }
        }
        String queryParams = "token=%s&email=%s".formatted(token, user.getEmail());
        String encodedQueryParams = Base64.getUrlEncoder().encodeToString(queryParams.getBytes(StandardCharsets.UTF_8));
        String link = "%s?params=".formatted(registrationUrl) + encodedQueryParams;
        String subject = "Welcome to the %s user registration".formatted(restaurantName);
        String toEmail = user.getEmail();
        publishToInvitationEventListener(toEmail, subject, link);
    }

    private void publishToInvitationEventListener(String toEmail, String subject, String link) {
        String body = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <title>You're Invited to Join</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
              <table align="center" width="100%%" style="max-width: 600px; background-color: #ffffff; padding: 20px; border-radius: 8px;">
                <tr>
                  <td style="text-align: center;">
                    <h2 style="color: #333;">You're Invited to Join Just In Time!</h2>
                    <p style="color: #555;">Click the button below to complete your registration and join our platform.</p>
                    <a href="%s" style="display: inline-block; padding: 12px 20px; margin: 20px 0; font-size: 16px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px;">
                      Complete Registration
                    </a>
                    <p style="color: #888;">Or copy and paste this link into your browser:</p>
                    <p style="color: #007bff;">%s</p>
                    <br/>
                    <p style="color: #aaa; font-size: 12px;">This link will expire in 24 hours for security reasons.</p>
                  </td>
                </tr>
              </table>
            </body>
            </html>
        """.formatted(link, link);
        eventPublisher.publishEvent(new UserInvitationEvent(this, toEmail, subject, body));
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipal(user);
    }

    private void addPermissionsToUser(User user, Set<String> permissionCodes) {
        Set<Permissions> permissions = permissionsService.getAllPermissionsByPermissionCodes(permissionCodes);
        user.setPermissions(permissions);
    }
}
