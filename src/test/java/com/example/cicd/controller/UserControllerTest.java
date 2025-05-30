package com.example.cicd.controller;

import com.example.cicd.model.User;
import com.example.cicd.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    public void setUp() {
        testUser1 = new User(1L, "John Doe", "john@example.com", "Engineering");
        testUser2 = new User(2L, "Jane Smith", "jane@example.com", "Marketing");
    }

    // ========== PASSING TESTS (6) ==========

    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(testUser1, testUser2);
        when(userService.getAllUsers()).thenReturn(users);
        when(userService.getActiveUsers()).thenReturn(2L);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.length()").value(2))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.users[0].name").value("John Doe"));
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser1);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.department").value("Engineering"));
    }

    @Test
    public void testCreateUserValid() throws Exception {
        User newUser = new User(null, "Alice Brown", "alice@example.com", "HR");
        User createdUser = new User(3L, "Alice Brown", "alice@example.com", "HR");

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value("Alice Brown"))
                .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    @Test
    public void testHealthCheck() throws Exception {
        when(userService.getTotalUsers()).thenReturn(2L);

        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"))
                .andExpect(jsonPath("$.service").value("User Management Service"));
    }

    @Test
    public void testGetUsersByDepartment() throws Exception {
        List<User> engineeringUsers = Arrays.asList(testUser1);
        when(userService.getUsersByDepartment("Engineering")).thenReturn(engineeringUsers);

        mockMvc.perform(get("/api/users/department/Engineering"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    // ========== FIXED TESTS (3) ==========

    @Test
    public void testCreateUserInvalidData() throws Exception {
        // Create user with invalid data (empty name and invalid email)
        User invalidUser = new User(null, "", "invalid-email", "");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest()); // FIXED - expects 400 Bad Request
    }

    @Test
    public void testGetNonExistentUser() throws Exception {
        when(userService.getUserById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound()); // FIXED - expects 404 Not Found
    }

    @Test
    public void testUpdateUser() throws Exception {
        User updatedUser = new User(1L, "Updated Name", "updated@example.com", "Updated Dept");
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value("Updated Name"))
                .andExpect(jsonPath("$.user.email").value("updated@example.com"))
                .andExpect(jsonPath("$.user.department").value("Updated Dept"))
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }
}