package com.justintime.jit.service;

import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.entity.Permissions;
import com.justintime.jit.entity.RestaurantRole;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

public interface PermissionsService {
    List<PermissionsDTO> getAllPermissionsByUserEmail(String email);
    List<PermissionsDTO> getAllPermissionsByRole(String role);
    boolean hasPermission(String permissionCode);
    void addPermissionsToUser(String email, Set<String> permissionCodes) throws AccessDeniedException;
    void addPermissionsToRole(String roleName, Set<String> permissionCodes);
    void updatePermissions(String roleOrEmail, boolean isRole, List<PermissionsDTO> permissionsDTOList) throws AccessDeniedException;
    Set<Permissions> getAllPermissionsByPermissionCodes(Set<String> permissionCodes);
    List<PermissionsDTO> getPermissionsByRoleName(String roleName);
}
