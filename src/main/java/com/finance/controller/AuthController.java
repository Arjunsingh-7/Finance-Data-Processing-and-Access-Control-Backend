package com.finance.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.finance.dto.JwtResponse;
import com.finance.dto.LoginRequest;
import com.finance.dto.UserCreateRequest;
import com.finance.dto.UserResponse;
import com.finance.entity.User;
import com.finance.security.JwtTokenProvider;
import com.finance.service.UserService;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse userResponse = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Fetch user
            User user = userService.getUserEntityByUsername(request.getUsername());

        
            System.out.println("========== LOGIN DEBUG ==========");
            System.out.println("USERNAME: " + request.getUsername());
            System.out.println("DB USER: " + user.getUsername());
            System.out.println("DB HASH: " + user.getPasswordHash());
            System.out.println("RAW PASSWORD: " + request.getPassword());

            boolean match = userService.validatePassword(
                    request.getPassword(),
                    user.getPasswordHash()
            );

            System.out.println("PASSWORD MATCH: " + match);
            System.out.println("================================");
        
            if (!match) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid username or password");
            }

    
            if (!user.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("User account is inactive");
            }

            
            String token = jwtTokenProvider.generateToken(
                    user.getUsername(),
                    user.getRole().getValue()
            );

            JwtResponse response = JwtResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole().getValue())
                    .build();

            log.info("User logged in successfully: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); 
            log.error("Login error: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }
}