package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.entity.RestaurantRole;
import com.justintime.jit.service.PermissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/jit-api/permissions")
public class PermissionsController extends BaseController {

    @Autowired
    private PermissionsService permissionsService;

    @PostMapping("/role/{roleName}")
    @PreAuthorize("hasPermission(null, 'ADD_PERMISSIONS')")
    public ResponseEntity<ApiResponse<String>> addPermissionsToRole(
            @PathVariable String roleName, 
            @RequestBody Set<String> permissionCodes) {
        permissionsService.addPermissionsToRole(roleName, permissionCodes);
        return success("Permissions added to role successfully");
    }

    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasPermission(null, 'VIEW_PERMISSIONS')")
    public ResponseEntity<ApiResponse<List<PermissionsDTO>>> getPermissionsByRoleName(
            @PathVariable String roleName) {
        return success(permissionsService.getPermissionsByRoleName(roleName), 
                "Permissions fetched successfully!");
    }

    @GetMapping("/{role}")
    @PreAuthorize("hasPermission(null, 'VIEW_PERMISSIONS')")
    public ResponseEntity<ApiResponse<List<PermissionsDTO>>> getPermissionsByRole(@PathVariable String role) {
        return success(permissionsService.getAllPermissionsByRole(role), "Permissions fetched successfully!");
    }
}
