package com.apartment.controller;

import com.apartment.dto.PaymentRequest;
import com.apartment.entity.*;
import com.apartment.repository.ComplaintRepository;
import com.apartment.security.CustomUserDetails;
import com.apartment.service.ResidentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resident")
@CrossOrigin(origins = "http://localhost:8080")
public class ResidentController {

    private final ResidentService residentService;
    private final ComplaintRepository complaintRepository;

    public ResidentController(ResidentService residentService, ComplaintRepository complaintRepository) {
        this.residentService = residentService;
        this.complaintRepository = complaintRepository;
    }

    @GetMapping("/announcements")
    public ResponseEntity<List<Announcement>> getAnnouncements() {
        // Use default apartment ID 1 for now
        return ResponseEntity.ok(residentService.getAnnouncements(1L));
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getMyPayments() {
        // Use default user ID 1 for now
        return ResponseEntity.ok(residentService.getUserPayments(1L));
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getExpenses() {
        // Use default apartment ID 1 for now
        return ResponseEntity.ok(residentService.getExpenses(1L));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<MonthlyReport>> getMonthlyReports() {
        // Use default apartment ID 1 for now
        return ResponseEntity.ok(residentService.getMonthlyReports(1L));
    }

    @PostMapping("/payments")
    public ResponseEntity<Payment> makePayment(@Valid @RequestBody PaymentRequest request) {
        // Use default user ID 1 for now
        Payment payment = residentService.makePayment(1L, request);
        // For demo, immediately mark as paid
        payment = residentService.updatePaymentStatus(payment.getId(), Payment.PaymentStatus.PAID, "DEMO_" + System.currentTimeMillis());
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/payments/{paymentId}/complete")
    public ResponseEntity<Payment> completePayment(@PathVariable Long paymentId,
                                                     @RequestParam String transactionId) {
        Payment payment = residentService.updatePaymentStatus(paymentId, Payment.PaymentStatus.PAID, transactionId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications() {
        // Use default user ID 1 for now
        return ResponseEntity.ok(residentService.getNotifications(1L));
    }

    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId) {
        residentService.markNotificationAsRead(notificationId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification marked as read");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        // Use default apartment ID 1 and user ID 1 for now
        Long apartmentId = 1L;
        Long userId = 1L;

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("announcements", residentService.getAnnouncements(apartmentId));
        dashboard.put("payments", residentService.getUserPayments(userId));
        dashboard.put("expenses", residentService.getExpenses(apartmentId));
        dashboard.put("reports", residentService.getMonthlyReports(apartmentId));
        dashboard.put("notifications", residentService.getNotifications(userId));
        dashboard.put("unreadCount", residentService.getNotifications(userId).stream()
                .filter(n -> !n.getRead()).count());

        return ResponseEntity.ok(dashboard);
    }

    // Complaint endpoints
    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getMyComplaints() {
        // Use default user ID 1 for now
        return ResponseEntity.ok(complaintRepository.findByUserId(1L));
    }

    @PostMapping("/complaints")
    public ResponseEntity<Complaint> createComplaint(@Valid @RequestBody Complaint complaint) {
        // Set default apartment and user IDs
        // In production, this would use the authenticated user's apartment
        complaint.setApartment(new Apartment());
        complaint.getApartment().setId(1L);
        complaint.setUser(new User());
        complaint.getUser().setId(1L);
        complaint.setStatus(Complaint.ComplaintStatus.PENDING);
        
        Complaint savedComplaint = complaintRepository.save(complaint);
        return ResponseEntity.ok(savedComplaint);
    }

    @PutMapping("/complaints/{complaintId}")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Long complaintId,
                                                      @RequestBody Complaint complaint) {
        Complaint existing = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        existing.setTitle(complaint.getTitle());
        existing.setDescription(complaint.getDescription());
        existing.setCategory(complaint.getCategory());
        
        return ResponseEntity.ok(complaintRepository.save(existing));
    }
}
