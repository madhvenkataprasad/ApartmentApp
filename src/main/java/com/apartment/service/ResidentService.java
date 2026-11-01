package com.apartment.service;

import com.apartment.dto.PaymentRequest;
import com.apartment.entity.*;
import com.apartment.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResidentService {

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final AnnouncementRepository announcementRepository;
    private final ExpenseRepository expenseRepository;
    private final MonthlyReportRepository monthlyReportRepository;
    private final NotificationRepository notificationRepository;

    public ResidentService(UserRepository userRepository, PaymentRepository paymentRepository, 
                          AnnouncementRepository announcementRepository, ExpenseRepository expenseRepository, 
                          MonthlyReportRepository monthlyReportRepository, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.announcementRepository = announcementRepository;
        this.expenseRepository = expenseRepository;
        this.monthlyReportRepository = monthlyReportRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<Announcement> getAnnouncements(Long apartmentId) {
        return announcementRepository.findByApartmentIdAndPublishedTrueOrderByCreatedAtDesc(apartmentId);
    }

    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Expense> getExpenses(Long apartmentId) {
        return expenseRepository.findByApartmentIdOrderByCreatedAtDesc(apartmentId);
    }

    public List<MonthlyReport> getMonthlyReports(Long apartmentId) {
        return monthlyReportRepository.findByApartmentIdAndPublishedTrueOrderByYearDescMonthDesc(apartmentId);
    }

    @Transactional
    public Payment makePayment(Long userId, PaymentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Payment payment = Payment.builder()
                .user(user)
                .apartment(user.getApartment())
                .amount(request.getAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .status(Payment.PaymentStatus.PENDING)
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .notes(request.getNotes())
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(status);
        payment.setTransactionId(transactionId);

        if (status == Payment.PaymentStatus.PAID) {
            Notification notification = Notification.builder()
                    .user(payment.getUser())
                    .title("Payment Successful")
                    .message("Your payment of ₹" + payment.getAmount() + " for " + payment.getMonth() + "/" + payment.getYear() + " has been received.")
                    .type(Notification.NotificationType.PAYMENT)
                    .build();
            notificationRepository.save(notification);
        }

        return paymentRepository.save(payment);
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
