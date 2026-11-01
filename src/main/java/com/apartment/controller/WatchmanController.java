package com.apartment.controller;

import com.apartment.entity.Announcement;
import com.apartment.entity.Complaint;
import com.apartment.repository.ComplaintRepository;
import com.apartment.security.CustomUserDetails;
import com.apartment.service.WatchmanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchman")
@CrossOrigin(origins = "http://localhost:8080")
public class WatchmanController {

    private final WatchmanService watchmanService;
    private final ComplaintRepository complaintRepository;

    public WatchmanController(WatchmanService watchmanService, ComplaintRepository complaintRepository) {
        this.watchmanService = watchmanService;
        this.complaintRepository = complaintRepository;
    }

    @GetMapping("/announcements")
    public ResponseEntity<List<Announcement>> getMyAnnouncements() {
        // Use default user ID 1 for now
        return ResponseEntity.ok(watchmanService.getWatchmanAnnouncements(1L));
    }

    @PostMapping("/announcements")
    public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestBody Announcement announcement) {
        // Use default user ID 1 for now
        return ResponseEntity.ok(watchmanService.createAnnouncement(1L, announcement));
    }

    @PutMapping("/announcements/{announcementId}")
    public ResponseEntity<Announcement> updateAnnouncement(@PathVariable Long announcementId,
                                                             @Valid @RequestBody Announcement announcement) {
        return ResponseEntity.ok(watchmanService.updateAnnouncement(announcementId, announcement));
    }

    @DeleteMapping("/announcements/{announcementId}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long announcementId) {
        watchmanService.deleteAnnouncement(announcementId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Announcement deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Complaint endpoints
    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        // Use default apartment ID 1 for now
        return ResponseEntity.ok(complaintRepository.findByApartmentId(1L));
    }

    @PutMapping("/complaints/{complaintId}/resolve")
    public ResponseEntity<Complaint> resolveComplaint(@PathVariable Long complaintId,
                                                   @RequestParam String resolution) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        complaint.setStatus(Complaint.ComplaintStatus.RESOLVED);
        complaint.setResolution(resolution);
        
        return ResponseEntity.ok(complaintRepository.save(complaint));
    }

    @PutMapping("/complaints/{complaintId}/status")
    public ResponseEntity<Complaint> updateComplaintStatus(@PathVariable Long complaintId,
                                                           @RequestParam Complaint.ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        complaint.setStatus(status);
        
        return ResponseEntity.ok(complaintRepository.save(complaint));
    }
}
