package com.apartment.repository;

import com.apartment.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    Optional<Apartment> findByName(String name);

    Optional<Apartment> findBySecretCode(String secretCode);

    boolean existsByName(String name);
}
