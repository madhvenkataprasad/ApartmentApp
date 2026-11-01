package com.apartment.repository;

import com.apartment.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByApartmentId(Long apartmentId);
    List<Complaint> findByUserId(Long userId);
    List<Complaint> findByStatus(Complaint.ComplaintStatus status);
}
