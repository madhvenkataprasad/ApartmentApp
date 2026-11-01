package com.apartment.repository;

import com.apartment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);

    List<Payment> findByApartmentId(Long apartmentId);

    List<Payment> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    List<Payment> findByApartmentIdAndMonthAndYear(Long apartmentId, Integer month, Integer year);

    @Query("SELECT p FROM Payment p WHERE p.apartment.id = :apartmentId AND p.month = :month AND p.year = :year AND p.status = 'PAID'")
    List<Payment> findPaidPaymentsByApartmentAndMonthYear(Long apartmentId, Integer month, Integer year);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.apartment.id = :apartmentId AND p.month = :month AND p.year = :year AND p.status = 'PAID'")
    Long countPaidPaymentsByApartmentAndMonthYear(Long apartmentId, Integer month, Integer year);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.apartment.id = :apartmentId AND p.month = :month AND p.year = :year AND p.status = 'PAID'")
    Double sumPaidAmountByApartmentAndMonthYear(Long apartmentId, Integer month, Integer year);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
