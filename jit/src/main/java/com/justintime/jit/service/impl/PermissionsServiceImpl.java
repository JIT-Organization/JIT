package com.justintime.jit.service.impl;

import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.Permissions;
import com.justintime.jit.entity.RestaurantRole;
import com.justintime.jit.entity.User;
import com.justintime.jit.repository.PermissionsRepository;
import com.justintime.jit.repository.RestaurantRoleRepository;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.PermissionsService;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionsServiceImpl implements PermissionsService {

    public PermissionsServiceImpl(PermissionsRepository permissionsRepository, UserRepository userRepository, RestaurantRoleRepository restaurantRoleRepository) {
        this.permissionsRepository = permissionsRepository;
        this.userRepository = userRepository;
        this.restaurantRoleRepository = restaurantRoleRepository;
    }

    private final PermissionsRepository permissionsRepository;
    private final UserRepository userRepository;
    private final RestaurantRoleRepository restaurantRoleRepository;

    private final GenericMapper<Permissions, PermissionsDTO> permissionsMapper = MapperFactory.getMapper(Permissions.class, PermissionsDTO.class);

    @Override
    @Transactional(readOnly = true)
    public List<PermissionsDTO> getAllPermissionsByUserEmail(String email) {
        return userRepository.findByEmail(email).getRestaurantRole().getPermissions().stream()
                .map(permissionsMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionsDTO> getAllPermissionsByRole(String role) {
        return permissionsRepository.findAllByRole(Role.valueOf(role.toUpperCase())).stream().map(permissionsMapper::toDto).toList();
    }

    @Override
    public boolean hasPermission(String permission) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).getRestaurantRole().getPermissions().stream()
                .anyMatch(p -> permission.equalsIgnoreCase(p.getTitle()));
    }

    @Override
    @Transactional
    public void addPermissionsToUser(String email, Set<String> permissionCodes) throws AccessDeniedException {
        User user = userRepository.findByEmail(email);
        Set<Permissions> permissions = new HashSet<>(
                permissionsRepository.findAllByPermissionCodeIn(permissionCodes)
        );
        validateRoleInPermissionsAndUser(user, permissions);
        user.getRestaurantRole().setPermissions(permissions);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void addPermissionsToRole(String roleName, Set<String> permissionCodes) {
        // Extract restaurant code from security context
        String restaurantCode = getRestaurantCodeFromContext();
        
        RestaurantRole role = restaurantRoleRepository.findByNameAndRestaurantCode(roleName, restaurantCode)
                .orElseThrow(() -> new RuntimeException("Restaurant role not found"));
        
        Set<Permissions> permissions = permissionsRepository.findAllByPermissionCodeIn(permissionCodes);
        
        // Add permissions to the role's permission set
        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }
        role.getPermissions().addAll(permissions);
        
        restaurantRoleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionsDTO> getPermissionsByRoleName(String roleName) {
        // Extract restaurant code from security context
        String restaurantCode = getRestaurantCodeFromContext();
        
        RestaurantRole role = restaurantRoleRepository.findByNameAndRestaurantCode(roleName, restaurantCode)
                .orElseThrow(() -> new RuntimeException("Restaurant role not found"));
        
        List<Permissions> permissions = permissionsRepository.findAllByRestaurantRoleId(role.getId());
        return permissions.stream()
                .map(permissionsMapper::toDto)
                .toList();
    }

    private String getRestaurantCodeFromContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof com.justintime.jit.security.CustomAuthToken) {
            return ((com.justintime.jit.security.CustomAuthToken) authentication).getRestaurantCode();
        }
        throw new RuntimeException("Restaurant code not found in security context");
    }

    @Override
    @Transactional
    public void updatePermissions(String roleOrEmail, boolean isRole, List<PermissionsDTO> newPermissionsDTOList) throws AccessDeniedException {
        Set<Permissions> newPermissions = newPermissionsDTOList.stream()
                .map(permissionsMapper::toEntity)
                .collect(Collectors.toSet());

        if (isRole) {
            // Extract restaurant code from security context
            String restaurantCode = getRestaurantCodeFromContext();
            
            // Find the role by name and restaurant code
            RestaurantRole restaurantRole = restaurantRoleRepository.findByNameAndRestaurantCode(roleOrEmail, restaurantCode)
                    .orElseThrow(() -> new RuntimeException("Restaurant role not found"));
            
            // Clear existing permissions and set new ones
            restaurantRole.getPermissions().clear();
            
            // Get permission codes from DTOs
            Set<String> permissionCodes = newPermissionsDTOList.stream()
                    .map(PermissionsDTO::getPermissionCode)
                    .collect(Collectors.toSet());
            
            Set<Permissions> persistedPermissions = permissionsRepository.findAllByPermissionCodeIn(permissionCodes);
            restaurantRole.getPermissions().addAll(persistedPermissions);
            
            restaurantRoleRepository.save(restaurantRole);
        } else {
            User user = userRepository.findByEmail(roleOrEmail);
            validateRoleInPermissionsAndUser(user, newPermissions);
            Set<Permissions> persistedPermissions = new HashSet<>(permissionsRepository.saveAll(newPermissions));
            user.getRestaurantRole().setPermissions(persistedPermissions);
            userRepository.save(user);
        }
    }

    @Override
    public Set<Permissions> getAllPermissionsByPermissionCodes(Set<String> permissionCodes) {
        return permissionsRepository.findAllByPermissionCodeIn(permissionCodes);
    }

    private void validateRoleInPermissionsAndUser(User user, Set<Permissions> permissions) throws AccessDeniedException {
        RestaurantRole userRole = user.getRestaurantRole();
        if (userRole == null) {
            throw new AccessDeniedException("User does not have a role assigned");
        }
        
        for (Permissions permission : permissions) {
            // Check if the permission is associated with the user's role
            boolean hasPermission = permission.getRestaurantRoles().stream()
                    .anyMatch(role -> role.getId().equals(userRole.getId()));
            
            if (!hasPermission) {
                throw new AccessDeniedException(String.format(
                        "User is not allowed to assign or access permission '%s'",
                        permission.getPermissionCode()
                ));
            }
        }
    }
}
