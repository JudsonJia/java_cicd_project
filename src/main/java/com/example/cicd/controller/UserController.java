package com.example.cicd.controller;

import com.example.cicd.model.User;
import com.example.cicd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("total", users.size());
        response.put("active", userService.getActiveUsers());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(
            @Valid @RequestBody User user,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("error", "Validation failed");
            response.put("details", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }

        User createdUser = userService.createUser(user);
        response.put("user", createdUser);
        response.put("message", "User created successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("error", "Validation failed");
            response.put("details", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }

        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            response.put("user", updatedUser);
            response.put("message", "User updated successfully");
            return ResponseEntity.ok(response);
        }

        response.put("error", "User not found");
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        Map<String, Object> response = new HashMap<>();

        if (deleted) {
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        }

        response.put("error", "User not found");
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<User>> getUsersByDepartment(@PathVariable String department) {
        List<User> users = userService.getUsersByDepartment(department);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_users", userService.getTotalUsers());
        stats.put("active_users", userService.getActiveUsers());
        stats.put("average_name_length", userService.calculateAverageNameLength());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("service", "User Management Service");
        health.put("timestamp", System.currentTimeMillis());
        health.put("total_users", userService.getTotalUsers());
        return ResponseEntity.ok(health);
    }
}