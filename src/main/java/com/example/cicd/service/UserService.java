package com.example.cicd.service;

import com.example.cicd.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserService() {
        // Initialize with sample data
        users.add(new User(1L, "John Doe", "john@example.com", "Engineering"));
        users.add(new User(2L, "Jane Smith", "jane@example.com", "Marketing"));
        idGenerator.set(3);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User getUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.add(user);
        return user;
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);
        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setDepartment(updatedUser.getDepartment());
            existingUser.setActive(updatedUser.isActive());
            return existingUser;
        }
        return null;
    }

    public boolean deleteUser(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }

    public List<User> getUsersByDepartment(String department) {
        return users.stream()
                .filter(user -> department.equals(user.getDepartment()))
                .collect(Collectors.toList());
    }

    public long getTotalUsers() {
        return users.size();
    }

    public long getActiveUsers() {
        return users.stream()
                .filter(User::isActive)
                .count();
    }

    // Method with intentional bug for testing
    public double calculateAverageNameLength() {
        if (users.isEmpty()) {
            return 0.0; // This might cause issues in some scenarios
        }

        double total = users.stream()
                .mapToDouble(user -> user.getName().length())
                .sum();

        return total / users.size();
    }

    // Email validation method with intentional weakness
    public boolean isValidEmail(String email) {
        // Intentionally simple validation that will fail some tests
        return email != null && email.contains("@");
    }

    // Additional utility methods
    public List<User> getActiveUsersList() {
        return users.stream()
                .filter(User::isActive)
                .collect(Collectors.toList());
    }

    public boolean userExistsById(Long id) {
        return getUserById(id) != null;
    }

    public boolean userExistsByEmail(String email) {
        return users.stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }
}