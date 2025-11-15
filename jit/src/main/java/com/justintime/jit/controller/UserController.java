package com.justintime.jit.controller;

import com.justintime.jit.dto.*;
import com.justintime.jit.entity.User;
import com.justintime.jit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    // GET: Retrieve all users
//    @GetMapping
//    public ResponseEntity<List<User>> getAllUsers() {
//        List<User> users = userService.findAll();
//        return ResponseEntity.ok(users);
//    }

    @GetMapping("/getCookNames")
    public ResponseEntity<ApiResponse<List<String>>> getCookNamesByRestaurantCode() {
        List<String> cookNames = userService.getCookNamesByRestaurantCode();
        return success(cookNames);
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'VIEW_USERS')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRestaurantCode() {
        List<UserDTO> users = userService.getUsersByRestaurantCode();
        return success(users);
    }

    // PUT: Update a user by ID
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'EDIT_USERS')")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.update(id, user);
        return ResponseEntity.ok("User updated successfully");
    }

    @PatchMapping("/{username}")
    @PreAuthorize("hasPermission(null, 'EDIT_USERS')")
    public ResponseEntity<ApiResponse<UserDTO>> patchUpdateUser(@PathVariable String username, @RequestBody PatchRequest<UserDTO> patchRequest) {
        UserDTO updatedUser = userService.patchUpdateUser(username, patchRequest.getDto(), patchRequest.getPropertiesToBeUpdated());
        return success(updatedUser);
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'ADD_USERS')")
    public ResponseEntity<ApiResponse<UserDTO>> addUser(@RequestBody UserDTO addUserRequest) {
        return success(userService.addUser(addUserRequest), "User Added Successfully");
    }

    @PostMapping("/send-invite")
    @PreAuthorize("hasPermission(null, 'ADD_USERS')")
    public ResponseEntity<ApiResponse<UserDTO>> inviteUser(@RequestBody UserDTO inviteUserDTO) throws IOException {
        userService.sendInviteToUser(inviteUserDTO);
        return success(null, "Invitation sent successfully");
    }

    // DELETE: Delete a user by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'DELETE_USERS')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // GET: Search users by username (or other parameters)
    @GetMapping("/search")
    @PreAuthorize("hasPermission(null, 'VIEW_USERS')")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String userName) {
        List<User> users = userService.findByUserName(userName);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/add-permissions")
    @PreAuthorize("hasPermission(null, 'ADD_PERMISSIONS')")
    public ResponseEntity<ApiResponse<UserDTO>> addPermissions(@RequestBody AddPermissionRequest addPermissionRequest) throws AccessDeniedException {
        return success(userService.addOrUpdatePermissions(addPermissionRequest.getEmail(), addPermissionRequest.getPermissionsDTOS(), false));
    }

    @PatchMapping("/update-permissions")
    @PreAuthorize("hasPermission(null, 'EDIT_PERMISSIONS')")
    public ResponseEntity<ApiResponse<UserDTO>> updatePermissions(@RequestBody AddPermissionRequest addPermissionRequest) throws AccessDeniedException {
        return success(userService.addOrUpdatePermissions(addPermissionRequest.getEmail(), addPermissionRequest.getPermissionsDTOS(), true));
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasPermission(null, 'VIEW_PERMISSIONS')")
    public ResponseEntity<ApiResponse<List<PermissionsDTO>>> getAllPermissions() {
        return success(userService.getAllPermissions());
    }

}
