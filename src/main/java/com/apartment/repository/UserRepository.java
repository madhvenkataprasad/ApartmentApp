package com.apartment.repository;

import com.apartment.entity.Apartment;
import com.apartment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByApartmentId(Long apartmentId);

    List<User> findByApartment(Apartment apartment);

    List<User> findByApartmentIdAndRole(Long apartmentId, User.Role role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.apartment.id = :apartmentId AND u.role = 'RESIDENT'")
    List<User> findResidentsByApartmentId(Long apartmentId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.apartment.id = :apartmentId AND u.role = 'RESIDENT'")
    Long countResidentsByApartmentId(Long apartmentId);
}
