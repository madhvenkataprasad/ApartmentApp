package com.apartment.config;

import com.apartment.entity.*;
import com.apartment.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ApartmentRepository apartmentRepository, UserRepository userRepository, 
                          PasswordEncoder passwordEncoder) {
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Check if apartment exists and update secret code to plain text
        Apartment apartment = apartmentRepository.findByName("Satya Hyma Residency").orElse(null);

        if (apartment == null) {
            // Create new apartment
            apartment = new Apartment();
            apartment.setName("Satya Hyma Residency");
            apartment.setSecretCode("SATYA2024");
            apartment.setAddress("123 Main Street");
            apartment.setCity("Hyderabad");
            apartment.setState("Telangana");
            apartment.setTotalFlats(50);
            apartment.setMonthlyMaintenanceAmount(1500.0);
            apartmentRepository.save(apartment);
            System.out.println("Default apartment created: Satya Hyma Residency");
        } else {
            // Update existing apartment's secret code to plain text
            apartment.setSecretCode("SATYA2024");
            apartmentRepository.save(apartment);
            System.out.println("Apartment secret code updated to plain text");
        }

        // Initialize default watchman if not exists
        if (!userRepository.existsByUsername("watchman")) {
            Apartment savedApartment = apartmentRepository.findByName("Satya Hyma Residency").orElseThrow();

            User watchman = new User();
            watchman.setUsername("watchman");
            watchman.setEmail("watchman@apartment.com");
            watchman.setPassword(passwordEncoder.encode("watchman123"));
            watchman.setFlatNumber("WATCHMAN");
            watchman.setRole(User.Role.WATCHMAN);
            watchman.setActive(true);
            watchman.setApartment(savedApartment);
            userRepository.save(watchman);
            System.out.println("Default watchman created: username=watchman, password=watchman123");
        }

        // Initialize default resident if not exists
        if (!userRepository.existsByUsername("resident")) {
            Apartment savedApartment = apartmentRepository.findByName("Satya Hyma Residency").orElseThrow();

            User resident = new User();
            resident.setUsername("resident");
            resident.setEmail("resident@apartment.com");
            resident.setPassword(passwordEncoder.encode("resident123"));
            resident.setFlatNumber("101");
            resident.setRole(User.Role.RESIDENT);
            resident.setActive(true);
            resident.setApartment(savedApartment);
            userRepository.save(resident);
            System.out.println("Default resident created: username=resident, password=resident123");
        }

        // Initialize default treasurer if not exists
        if (!userRepository.existsByUsername("treasurer")) {
            Apartment savedApartment = apartmentRepository.findByName("Satya Hyma Residency").orElseThrow();

            User treasurer = new User();
            treasurer.setUsername("treasurer");
            treasurer.setEmail("treasurer@apartment.com");
            treasurer.setPassword(passwordEncoder.encode("treasurer123"));
            treasurer.setFlatNumber("102");
            treasurer.setRole(User.Role.TREASURER);
            treasurer.setActive(true);
            treasurer.setApartment(savedApartment);
            userRepository.save(treasurer);
            System.out.println("Default treasurer created: username=treasurer, password=treasurer123");
        }

        System.out.println("Data initialization completed");
        System.out.println("Apartment Secret Code: SATYA2024");
        System.out.println("Treasurer Key: TREASURER_SECRET_KEY_2024");
    }
}
