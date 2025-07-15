package com.justintime.jit.service;

import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.entity.Permissions;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

public interface PermissionsService {
    List<PermissionsDTO> getAllPermissionsByUserEmail(String email);
    List<PermissionsDTO> getAllPermissionsByRole(String role);
    boolean hasPermission(String permissionCode);
    void addPermissionsToUser(String email, Set<String> permissionCodes) throws AccessDeniedException;
    List<PermissionsDTO> addPermissionsToRole(String role, List<PermissionsDTO> permissionsDTOList);
    void updatePermissions(String roleOrEmail, boolean isRole, List<PermissionsDTO> permissionsDTOList) throws AccessDeniedException;
    Set<Permissions> getAllPermissionsByPermissionCodes(Set<String> permissionCodes);
}
