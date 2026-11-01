package com.apartment.service;

import com.apartment.dto.LoginRequest;
import com.apartment.dto.RegisterRequest;
import com.apartment.entity.Apartment;
import com.apartment.entity.User;
import com.apartment.repository.ApartmentRepository;
import com.apartment.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, ApartmentRepository apartmentRepository, 
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.apartmentRepository = apartmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerResident(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Find apartment by secret code (plain text comparison)
        Apartment apartment = apartmentRepository.findAll().stream()
                .filter(a -> a.getSecretCode().equals(request.getSecretCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid apartment secret code"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFlatNumber(request.getFlatNumber());
        user.setRole(User.Role.RESIDENT);
        user.setActive(true);
        user.setApartment(apartment);

        return userRepository.save(user);
    }

    @Transactional
    public User registerTreasurer(RegisterRequest request, String treasurerKey) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Find apartment by secret code (plain text comparison)
        Apartment apartment = apartmentRepository.findAll().stream()
                .filter(a -> a.getSecretCode().equals(request.getSecretCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid apartment secret code"));

        if (!treasurerKey.equals("TREASURER_SECRET_KEY_2024")) {
            throw new RuntimeException("Invalid treasurer key");
        }

        // Check if treasurer already exists for this apartment
        boolean treasurerExists = userRepository.findByApartment(apartment).stream()
                .anyMatch(user -> user.getRole() == User.Role.TREASURER);
        
        if (treasurerExists) {
            throw new RuntimeException("A treasurer already exists for this apartment. Only one treasurer is allowed.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFlatNumber(request.getFlatNumber());
        user.setRole(User.Role.TREASURER);
        user.setActive(true);
        user.setTreasurerKey(passwordEncoder.encode(treasurerKey));
        user.setApartment(apartment);

        return userRepository.save(user);
    }

    public void loginSuccess(String username, String ip) {
        // Account lockout removed - no action needed
    }

    public void loginFailure(String username, String ip) {
        // Account lockout removed - no action needed
    }

    public boolean validateApartmentSecretCode(String secretCode) {
        return apartmentRepository.findAll().stream()
                .anyMatch(apartment -> apartment.getSecretCode().equals(secretCode));
    }

    public boolean validateTreasurerKey(String treasurerKey) {
        return "TREASURER_SECRET_KEY_2024".equals(treasurerKey);
    }
}
