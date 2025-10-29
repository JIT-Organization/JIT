package com.justintime.jit.service;

import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.dto.UserDTO;
import com.justintime.jit.entity.RestaurantRole;
import com.justintime.jit.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;

public interface UserService extends BaseService<User, Long>, UserDetailsService {
    List<User> findByRestaurantRole(RestaurantRole restaurantRole);
    User findByEmail(String email);
    List<User> findByUserName(String username);
    List<String> getCookNamesByRestaurantCode(String restaurantCode);
    void delete(Long id);
    void update(Long id, User user);

    List<UserDTO> getUsersByRestaurantCode(String restaurantCode);

    User getUserByRestaurantCodeAndUsername(String restaurantCode, String username);

    UserDTO patchUpdateUser(String restaurantCode, String username, UserDTO dto, HashSet<String> propertiesToBeUpdated);

    UserDTO addOrUpdatePermissions(String email, List<PermissionsDTO> permissionsDTOS, boolean isEdit) throws AccessDeniedException;

    List<PermissionsDTO> getAllPermissions();

    UserDTO addUser(UserDTO addUserRequest);

    void sendInviteToUser(UserDTO inviteUserDTO) throws IOException;
}
