package com.justintime.jit.service.impl;

import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.Permissions;
import com.justintime.jit.entity.User;
import com.justintime.jit.repository.PermissionsRepository;
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

    public PermissionsServiceImpl(PermissionsRepository permissionsRepository, UserRepository userRepository) {
        this.permissionsRepository = permissionsRepository;
        this.userRepository = userRepository;
    }

    private final PermissionsRepository permissionsRepository;
    private final UserRepository userRepository;

    private final GenericMapper<Permissions, PermissionsDTO> permissionsMapper = MapperFactory.getMapper(Permissions.class, PermissionsDTO.class);

    @Override
    @Transactional(readOnly = true)
    public List<PermissionsDTO> getAllPermissionsByUserEmail(String email) {
        return userRepository.findByEmail(email).getPermissions().stream()
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
        return userRepository.findByEmail(email).getPermissions().stream()
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
        user.setPermissions(permissions);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public List<PermissionsDTO> addPermissionsToRole(String role, List<PermissionsDTO> permissionsDTOList) {
        Set<Permissions> newPermissions = new HashSet<>(
                permissionsDTOList.stream()
                        .map(dto -> {
                            Permissions entity = permissionsMapper.toEntity(dto);
                            entity.setRole(Role.valueOf(role.toUpperCase()));
                            return entity;
                        })
                        .toList()
        );
        Set<Permissions> savedPermissions = new HashSet<>(permissionsRepository.saveAll(newPermissions));
        return savedPermissions.stream()
                .map(permissionsMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void updatePermissions(String roleOrEmail, boolean isRole, List<PermissionsDTO> newPermissionsDTOList) throws AccessDeniedException {
        Set<Permissions> newPermissions = newPermissionsDTOList.stream()
                .map(permissionsMapper::toEntity)
                .collect(Collectors.toSet());

        if (isRole) {
            Role roleEnum;
            try {
                roleEnum = Role.valueOf(roleOrEmail.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid role: " + roleOrEmail);
            }
            List<Permissions> oldPermissions = permissionsRepository.findAllByRole(roleEnum);
            permissionsRepository.deleteAll(oldPermissions);
            newPermissions.forEach(p -> p.setRole(roleEnum));
            permissionsRepository.saveAll(newPermissions);
        } else {
            User user = userRepository.findByEmail(roleOrEmail);
            validateRoleInPermissionsAndUser(user, newPermissions);
            Set<Permissions> persistedPermissions = new HashSet<>(permissionsRepository.saveAll(newPermissions));
            user.setPermissions(persistedPermissions);
            userRepository.save(user);
        }
    }

    @Override
    public Set<Permissions> getAllPermissionsByPermissionCodes(Set<String> permissionCodes) {
        return permissionsRepository.findAllByPermissionCodeIn(permissionCodes);
    }

    private void validateRoleInPermissionsAndUser(User user, Set<Permissions> permissions) throws AccessDeniedException {
        for (Permissions permission : permissions) {
            if (permission.getRole().getValue() > user.getRole().getValue()) {
                throw new AccessDeniedException(String.format(
                        "User is not allowed to assign or access permission '%s'",
                        permission.getRole().name()
                ));
            }
        }
    }
}
