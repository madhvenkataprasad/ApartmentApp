package com.apartment.repository;

import com.apartment.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByApartmentIdOrderByCreatedAtDesc(Long apartmentId);

    List<Announcement> findByApartmentIdAndPublishedTrueOrderByCreatedAtDesc(Long apartmentId);

    List<Announcement> findByWatchmanIdOrderByCreatedAtDesc(Long watchmanId);
}
