package com.justintime.jit.service;

import com.justintime.jit.dto.LoginRequestDto;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends BaseService<User, Long>, UserDetailsService {
    List<User> findByRole(Role role);
    User findByEmail(String email);
    List<User> findByUsername(String username);
    void delete(Long id);
    void update(Long id, User user);
}
