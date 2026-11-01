package com.apartment.repository;

import com.apartment.entity.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {

    List<MonthlyReport> findByApartmentIdOrderByYearDescMonthDesc(Long apartmentId);

    Optional<MonthlyReport> findByApartmentIdAndMonthAndYear(Long apartmentId, Integer month, Integer year);

    List<MonthlyReport> findByApartmentIdAndPublishedTrueOrderByYearDescMonthDesc(Long apartmentId);
}
