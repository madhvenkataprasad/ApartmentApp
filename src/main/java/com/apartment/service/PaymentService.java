package com.apartment.service;

import com.apartment.dto.PaymentRequest;
import com.apartment.entity.Payment;
import com.apartment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${app.payment.razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${app.payment.razorpay.key_secret}")
    private String razorpayKeySecret;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Map<String, Object> createRazorpayOrder(Long paymentId, Double amount) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // In production, integrate with actual Razorpay API
        // For demo, we'll simulate the order creation
        String orderId = "order_" + UUID.randomUUID().toString().substring(0, 10);

        payment.setRazorpayOrderId(orderId);
        paymentRepository.save(payment);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("keyId", razorpayKeyId);
        response.put("amount", amount);
        response.put("currency", "INR");
        response.put("name", "Satya Hyma Residency");
        response.put("description", "Monthly Maintenance Payment");
        response.put("prefill", Map.of(
                "name", payment.getUser().getUsername(),
                "email", payment.getUser().getEmail()
        ));

        return response;
    }

    public Map<String, Object> verifyRazorpayPayment(String razorpayOrderId, String razorpayPaymentId,
                                                       String razorpaySignature) {
        // In production, verify signature using Razorpay SDK
        // For demo, we'll accept any payment
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setTransactionId(razorpayPaymentId);
        paymentRepository.save(payment);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Payment verified successfully");
        return response;
    }

    public Map<String, Object> generateUPIQRCode(Long paymentId, Double amount) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Generate UPI QR code data
        // Format: upi://pay?pa=VPA@bank&pn=Name&am=Amount&cu=INR
        String upiData = String.format(
                "upi://pay?pa=apartment@upi&pn=Satya Hyma Residency&am=%.2f&cu=INR&tn=%s",
                amount,
                "Maintenance-" + payment.getMonth() + "-" + payment.getYear()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("upiData", upiData);
        response.put("amount", amount);
        response.put("paymentId", paymentId);
        response.put("qrCodeUrl", "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + upiData);

        return response;
    }

    @Transactional
    public Payment recordManualPayment(Long paymentId, String transactionId, String notes) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setTransactionId(transactionId);
        payment.setNotes(notes);
        payment.setPaymentMethod(Payment.PaymentMethod.CASH);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment recordUPIPayment(Long paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setTransactionId(transactionId);
        payment.setPaymentMethod(Payment.PaymentMethod.UPI);

        return paymentRepository.save(payment);
    }
}
