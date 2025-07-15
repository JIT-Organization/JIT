package com.justintime.jit.service;

import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.dto.UserDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;

public interface UserService extends BaseService<User, Long>, UserDetailsService {
    List<User> findByRole(Role role);
    User findByEmail(String email);
    List<User> findByUsername(String username);
    void delete(Long id);
    void update(Long id, User user);

    List<UserDTO> getUsersByRestaurantCode(String restaurantCode);

    UserDTO patchUpdateUser(String restaurantCode, String username, UserDTO dto, HashSet<String> propertiesToBeUpdated);

    UserDTO addOrUpdatePermissions(String email, List<PermissionsDTO> permissionsDTOS, boolean isEdit) throws AccessDeniedException;

    List<PermissionsDTO> getAllPermissions();

    UserDTO addUser(UserDTO addUserRequest);

    void sendInviteToUser(UserDTO inviteUserDTO);
}
