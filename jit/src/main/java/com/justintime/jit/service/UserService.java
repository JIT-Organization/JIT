package com.justintime.jit.service;

import com.justintime.jit.dto.LoginRequestDto;
import com.justintime.jit.dto.UserDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;
import java.util.List;

public interface UserService extends BaseService<User, Long>, UserDetailsService {
    List<User> findByRole(Role role);
    User findByEmail(String email);
    List<User> findByUserName(String username);
    List<String> getCookNamesByRestaurantCode(String restaurantCode);
    void delete(Long id);
    void update(Long id, User user);

    List<UserDTO> getUsersByRestaurantCode(String restaurantCode);

    User getUserByRestaurantCodeAndUsername(String restaurantCode, String username);

    UserDTO patchUpdateUser(String restaurantCode, String username, UserDTO dto, HashSet<String> propertiesToBeUpdated);
}
