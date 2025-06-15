package com.justintime.jit.service.impl;

import com.justintime.jit.dto.UserDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.entity.User;
import com.justintime.jit.entity.UserPrincipal;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.UserService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

    private final UserRepository userRepository;

    private final RestaurantRepository restaurantRepository;

    private final CommonServiceImplUtil commonServiceImplUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RestaurantRepository restaurantRepository, CommonServiceImplUtil commonServiceImplUtil) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.commonServiceImplUtil = commonServiceImplUtil;
    }

    @Override
    public List<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
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
            existingUser.setUserName(updatedUser.getUserName());
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
    public UserDTO patchUpdateUser(String restaurantCode, String username, UserDTO dto, HashSet<String> propertiesToBeUpdated) {
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        User existingUser = userRepository.findByRestaurantIdAndUserName(restaurant.getId(), username);
        GenericMapper<User, UserDTO> userMapper = MapperFactory.getMapper(User.class, UserDTO.class);
        User patchedUser = userMapper.toEntity(dto);
        // TODO write a validation where the username should be unique if they are updating it
        HashSet<String> propertiesToBeUpdatedClone = new HashSet<>(propertiesToBeUpdated);
        commonServiceImplUtil.copySelectedProperties(patchedUser, existingUser, propertiesToBeUpdatedClone);
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
}
