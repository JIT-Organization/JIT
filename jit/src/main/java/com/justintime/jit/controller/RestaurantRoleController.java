package com.justintime.jit.controller;

import com.justintime.jit.dto.RestaurantRoleDTO;
import com.justintime.jit.service.RestaurantRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/restaurant-roles")
@Tag(name = "Restaurant Role Management", description = "APIs for managing restaurant roles")
public class RestaurantRoleController extends BaseController {

    @Autowired
    private RestaurantRoleService restaurantRoleService;

    @GetMapping("/{restaurantCode}")
    @PreAuthorize("hasPermission(null, 'VIEW_ROLES')")
    @Operation(summary = "Get all roles for a restaurant", description = "Retrieve all roles for a specific restaurant")
    public ResponseEntity<?> getRolesByRestaurantCode(@PathVariable String restaurantCode) {
        try {
            List<RestaurantRoleDTO> roles = restaurantRoleService.getRolesByRestaurantCode(restaurantCode);
            return success(roles);
        } catch (Exception e) {
            return error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{restaurantCode}")
    @PreAuthorize("hasPermission(null, 'ADD_ROLES')")
    @Operation(summary = "Create a new role", description = "Create a new role for a restaurant")
    public ResponseEntity<?> createRole(
            @PathVariable String restaurantCode,
            @RequestBody RestaurantRoleDTO roleDTO) {
        try {
            roleDTO.setRestaurantCode(restaurantCode);
            RestaurantRoleDTO createdRole = restaurantRoleService.createRole(roleDTO);
            return success(createdRole, "Role created successfully");
        } catch (Exception e) {
            return error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{restaurantCode}/{roleId}")
    @PreAuthorize("hasPermission(null, 'EDIT_ROLES')")
    @Operation(summary = "Update a role", description = "Update an existing role")
    public ResponseEntity<?> updateRole(
            @PathVariable String restaurantCode,
            @PathVariable Long roleId,
            @RequestBody RestaurantRoleDTO roleDTO) {
        try {
            roleDTO.setRestaurantCode(restaurantCode);
            RestaurantRoleDTO updatedRole = restaurantRoleService.updateRole(roleId, roleDTO);
            return success(updatedRole, "Role updated successfully");
        } catch (Exception e) {
            return error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{restaurantCode}/{roleId}")
    @PreAuthorize("hasPermission(null, 'DELETE_ROLES')")
    @Operation(summary = "Delete a role", description = "Delete a role by ID")
    public ResponseEntity<?> deleteRole(
            @PathVariable String restaurantCode,
            @PathVariable Long roleId) {
        try {
            restaurantRoleService.deleteRole(roleId);
            return success(null, "Role deleted successfully");
        } catch (Exception e) {
            return error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
