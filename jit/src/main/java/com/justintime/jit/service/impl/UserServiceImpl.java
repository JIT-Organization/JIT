package com.justintime.jit.service.impl;

import com.justintime.jit.dto.LoginRequestDto;
import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.UserDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.entity.User;
import com.justintime.jit.entity.UserPrincipal;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.JwtService;
import com.justintime.jit.service.UserService;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        GenericMapper<User, UserDTO> userMapper = MapperFactory.getMapper(User.class, UserDTO.class);
        return users.stream().map(userMapper::toDto).toList();
    }

    @Override
    public UserDTO patchUpdateUser(String restaurantCode, String username, UserDTO dto, List<String> propertiesToBeUpdated) {
        User existingUser = userRepository.findByRestaurantCodeAndUsername(restaurantCode, username);
        GenericMapper<User, UserDTO> userMapper = MapperFactory.getMapper(User.class, UserDTO.class);
        User patchedUser = userMapper.toEntity(dto);
        // TODO write a validation where the username should be unique if they are updating it
        HashSet<String> propertiesToBeUpdatedClone = new HashSet<>(propertiesToBeUpdated);
        copySelectedProperties(patchedUser, existingUser, propertiesToBeUpdatedClone);
        existingUser.setUpdatedDttm(LocalDateTime.now());
        userRepository.save(existingUser);
        return userMapper.toDto(existingUser);
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

    private void copySelectedProperties(Object source, Object target, HashSet<String> propertiesToBeChanged) {
        BeanWrapper srcWrapper = new BeanWrapperImpl(source);
        BeanWrapper targetWrapper = new BeanWrapperImpl(target);

        for (String property : propertiesToBeChanged) {
            if (srcWrapper.isReadableProperty(property) && srcWrapper.getPropertyValue(property) != null) {
                targetWrapper.setPropertyValue(property, srcWrapper.getPropertyValue(property));
            }
        }
    }
}
