package com.apartment.controller;

import com.apartment.entity.Payment;
import com.apartment.security.CustomUserDetails;
import com.apartment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:8080")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<Map<String, Object>> createRazorpayOrder(@RequestParam Long paymentId) {
        Map<String, Object> response = paymentService.createRazorpayOrder(paymentId, 0.0);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<Map<String, Object>> verifyRazorpayPayment(@RequestParam String razorpayOrderId,
                                                                        @RequestParam String razorpayPaymentId,
                                                                        @RequestParam String razorpaySignature) {
        Map<String, Object> response = paymentService.verifyRazorpayPayment(
                razorpayOrderId, razorpayPaymentId, razorpaySignature);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upi/qr-code")
    public ResponseEntity<Map<String, Object>> generateUPIQRCode(@RequestParam Long paymentId,
                                                                   @RequestParam Double amount) {
        Map<String, Object> response = paymentService.generateUPIQRCode(paymentId, amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/manual")
    public ResponseEntity<Payment> recordManualPayment(@RequestParam Long paymentId,
                                                         @RequestParam String transactionId,
                                                         @RequestParam(required = false) String notes) {
        Payment payment = paymentService.recordManualPayment(paymentId, transactionId, notes);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/upi")
    public ResponseEntity<Payment> recordUPIPayment(@RequestParam Long paymentId,
                                                     @RequestParam String transactionId) {
        Payment payment = paymentService.recordUPIPayment(paymentId, transactionId);
        return ResponseEntity.ok(payment);
    }
}
