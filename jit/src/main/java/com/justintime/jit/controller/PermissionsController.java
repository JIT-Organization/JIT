package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.PermissionsDTO;
import com.justintime.jit.service.PermissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/permissions")
public class PermissionsController extends BaseController {

    @Autowired
    private PermissionsService permissionsService;

    @PostMapping("/{role}")
    @PreAuthorize("hasPermission(null, 'ADD_PERMISSIONS')")
    public List<PermissionsDTO> addPermissionsToRole(@PathVariable String role, @RequestBody List<PermissionsDTO> permissionsDTOS) {
        return permissionsService.addPermissionsToRole(role, permissionsDTOS);
    }

    @GetMapping("/{role}")
    @PreAuthorize("hasPermission(null, 'VIEW_PERMISSIONS')")
    public ResponseEntity<ApiResponse<List<PermissionsDTO>>> getPermissionsByRole(@PathVariable String role) {
        return success(permissionsService.getAllPermissionsByRole(role), "Permissions fetched successfully!");
    }
}
