package com.finance.service;
import com.finance.dto.UserCreateRequest;
import com.finance.dto.UserResponse;
import com.finance.entity.Role;
import com.finance.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //  CREATE USER
    public UserResponse createUser(UserCreateRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        Role role = Role.fromValue(request.getRole());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        log.info("User created: {}", user.getUsername());

        return mapToResponse(user);
    }

    //  GET USER BY ID
    public UserResponse getUserById(Long id) {
        User user = getUserOrThrow(id);
        return mapToResponse(user);
    }

    // GET BY USERNAME
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return mapToResponse(user);
    }

    //  GET ENTITY
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    //  GET ALL USERS
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //  GET BY ROLE
    public List<UserResponse> getUsersByRole(String roleStr) {
        Role role = Role.fromValue(roleStr);

        return userRepository.findByRole(role)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //  GET ACTIVE USERS
    public List<UserResponse> getActiveUsers() {
        return userRepository.findByIsActive(true)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 UPDATE STATUS
    public UserResponse updateUserStatus(Long id, Boolean isActive) {
        User user = getUserOrThrow(id);

        user.setIsActive(isActive);

        return mapToResponse(userRepository.save(user));
    }

    // 🔹 UPDATE ROLE
    public UserResponse updateUserRole(Long id, String roleStr) {
        User user = getUserOrThrow(id);

        Role role = Role.fromValue(roleStr);
        user.setRole(role);

        return mapToResponse(userRepository.save(user));
    }

    //  DELETE USER
    public void deleteUser(Long id) {
        User user = getUserOrThrow(id);

        userRepository.delete(user);

        log.info("User deleted: {}", user.getUsername());
    }

    // 🔹 PASSWORD VALIDATION
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 🔹 COMMON METHOD
    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // 🔹 MAPPER
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}