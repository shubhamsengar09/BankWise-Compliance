package com.bankwise.aml_backend.controller;

import com.bankwise.aml_backend.config.JwtUtil;
import com.bankwise.aml_backend.config.CustomUserDetailsService;
import com.bankwise.aml_backend.model.User;
import com.bankwise.aml_backend.service.AmlService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final AmlService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService customUserDetailsService,
                          JwtUtil jwtUtil,
                          AmlService userService) {
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            final UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
            final String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PutMapping("/register")  // Changed to PUT method as you requested
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.registerOrUpdateUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
}
