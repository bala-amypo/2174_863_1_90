package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.model.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Login and Registration")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse> register(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully", registeredUser));
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        if (authentication.isAuthenticated()) {
            User user = userService.getAllUsers().stream()  
                    .filter(u -> u.getEmail().equals(request.getEmail()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found after auth"));
            
            String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
            return ResponseEntity.ok(new ApiResponse(true, "Login success",
                    new AuthResponse(token, user.getId(), user.getEmail(), user.getRole())));
        } else {
            return ResponseEntity.status(401).body(new ApiResponse(false, "Invalid credentials", null));
        }
    }
}
