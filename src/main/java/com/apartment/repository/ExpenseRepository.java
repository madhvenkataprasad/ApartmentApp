package com.apartment.repository;

import com.apartment.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByApartmentIdOrderByCreatedAtDesc(Long apartmentId);

    List<Expense> findByApartmentIdAndMonthAndYearOrderByCreatedAtDesc(Long apartmentId, Integer month, Integer year);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.apartment.id = :apartmentId AND e.month = :month AND e.year = :year")
    Optional<Double> sumExpensesByApartmentAndMonthYear(Long apartmentId, Integer month, Integer year);

    List<Expense> findByTreasurerIdOrderByCreatedAtDesc(Long treasurerId);
}
