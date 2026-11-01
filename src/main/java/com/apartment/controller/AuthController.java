package com.apartment.controller;

import com.apartment.dto.LoginRequest;
import com.apartment.dto.RegisterRequest;
import com.apartment.entity.User;
import com.apartment.security.CustomUserDetails;
import com.apartment.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8080")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerResident(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.registerResident(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("flatNumber", user.getFlatNumber());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/register/treasurer")
    public ResponseEntity<?> registerTreasurer(@Valid @RequestBody RegisterRequest request,
                                                @RequestParam String treasurerKey) {
        try {
            User user = authService.registerTreasurer(request, treasurerKey);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Treasurer registration successful");
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("flatNumber", user.getFlatNumber());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "forward:/login.html";
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Test endpoint works");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // Additional validation for resident login with secret code
            if (request.getSecretCode() != null && !request.getSecretCode().isEmpty()) {
                if (!authService.validateApartmentSecretCode(request.getSecretCode())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Invalid apartment secret code");
                    return ResponseEntity.status(401).body(error);
                }
            }

            // Additional validation for treasurer login with treasurer key
            if (request.getTreasurerKey() != null && !request.getTreasurerKey().isEmpty()) {
                if (!authService.validateTreasurerKey(request.getTreasurerKey())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Invalid treasurer key");
                    return ResponseEntity.status(401).body(error);
                }
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("username", userDetails.getUsername());
            response.put("email", userDetails.getEmail());
            response.put("role", userDetails.getRole().name());
            response.put("userId", userDetails.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/manual-login")
    public ResponseEntity<?> manualLogin(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            String redirectUrl = "/dashboard.html";
            String role = userDetails.getRole().name();
            
            // Handle both role formats: "RESIDENT" and "ROLE_RESIDENT"
            String cleanRole = role.replace("ROLE_", "");
            
            if (cleanRole.equals("WATCHMAN")) {
                redirectUrl = "/watchman-dashboard.html";
            } else if (cleanRole.equals("TREASURER")) {
                redirectUrl = "/treasurer-dashboard.html";
            } else {
                redirectUrl = "/dashboard.html";
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("redirectUrl", redirectUrl);
            response.put("username", userDetails.getUsername());
            response.put("role", role);
            response.put("cleanRole", cleanRole);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials: " + e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}
