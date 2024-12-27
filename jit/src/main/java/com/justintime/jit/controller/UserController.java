package com.justintime.jit.controller;

import com.justintime.jit.entity.User;
import com.justintime.jit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // POST: Register a new user
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // POST: Login a user
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        // Add logic for authentication
        return ResponseEntity.ok("Login successful");
    }

    // GET: Retrieve all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // GET: Retrieve a single user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    // PUT: Update a user by ID
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.update(id, user);
        return ResponseEntity.ok("User updated successfully");
    }

    // DELETE: Delete a user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // GET: Search users by username (or other parameters)
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String userName) {
        List<User> users = userService.findByUserName(userName);
        return ResponseEntity.ok(users);
    }
}
