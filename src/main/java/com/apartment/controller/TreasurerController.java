package com.apartment.controller;

import com.apartment.entity.Announcement;
import com.apartment.entity.Complaint;
import com.apartment.entity.Expense;
import com.apartment.entity.MonthlyReport;
import com.apartment.entity.Payment;
import com.apartment.repository.ComplaintRepository;
import com.apartment.security.CustomUserDetails;
import com.apartment.service.TreasurerService;
import com.apartment.service.WatchmanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/treasurer")
@CrossOrigin(origins = "http://localhost:8080")
public class TreasurerController {

    private final TreasurerService treasurerService;
    private final WatchmanService watchmanService;
    private final ComplaintRepository complaintRepository;

    public TreasurerController(TreasurerService treasurerService, WatchmanService watchmanService, ComplaintRepository complaintRepository) {
        this.treasurerService = treasurerService;
        this.watchmanService = watchmanService;
        this.complaintRepository = complaintRepository;
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long apartmentId = userDetails.getUser().getApartment().getId();
        return ResponseEntity.ok(treasurerService.getAllPayments(apartmentId));
    }

    @GetMapping("/payments/month")
    public ResponseEntity<List<Payment>> getPaymentsByMonthYear(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                   @RequestParam Integer month,
                                                                   @RequestParam Integer year) {
        Long apartmentId = userDetails.getUser().getApartment().getId();
        return ResponseEntity.ok(treasurerService.getPaymentsByMonthYear(apartmentId, month, year));
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        // Use default apartment ID 1 for now
        return ResponseEntity.ok(treasurerService.getAllExpenses(1L));
    }

    @GetMapping("/expenses/month")
    public ResponseEntity<List<Expense>> getExpensesByMonthYear(@RequestParam Integer month,
                                                                   @RequestParam Integer year) {
        // Use default apartment ID 1 for now
        return ResponseEntity.ok(treasurerService.getExpensesByMonthYear(1L, month, year));
    }

    @PostMapping("/expenses")
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        // Use default apartment ID 1 for now
        return ResponseEntity.ok(treasurerService.createExpense(1L, expense));
    }

    @PutMapping("/expenses/{expenseId}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long expenseId,
                                                   @Valid @RequestBody Expense expense) {
        return ResponseEntity.ok(treasurerService.updateExpense(expenseId, expense));
    }

    @DeleteMapping("/expenses/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long expenseId) {
        treasurerService.deleteExpense(expenseId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Expense deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reports/generate")
    public ResponseEntity<MonthlyReport> generateReport(@RequestParam Integer month,
                                                          @RequestParam Integer year) {
        // Use default user ID 1 for now
        return ResponseEntity.ok(treasurerService.generateMonthlyReport(1L, month, year));
    }

    @PostMapping("/reports/{reportId}/publish")
    public ResponseEntity<MonthlyReport> publishReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(treasurerService.publishReport(reportId));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<MonthlyReport>> getAllReports() {
        // Use default apartment ID 1 for now
        return ResponseEntity.ok(treasurerService.getAllReports(1L));
    }

    // Treasurer can also create announcements
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

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long apartmentId = userDetails.getUser().getApartment().getId();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("payments", treasurerService.getAllPayments(apartmentId));
        dashboard.put("expenses", treasurerService.getAllExpenses(apartmentId));
        dashboard.put("reports", treasurerService.getAllReports(apartmentId));
        dashboard.put("announcements", watchmanService.getWatchmanAnnouncements(userDetails.getId()));

        return ResponseEntity.ok(dashboard);
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
