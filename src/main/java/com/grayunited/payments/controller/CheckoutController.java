package com.grayunited.payments.controller;

import com.grayunited.payments.service.CheckoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class CheckoutController {

    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);

    private final CheckoutService checkoutService;

    @Value("${internal.api.secret}")
    private String internalApiSecret;

    // Constructor Injection (Best Practice)
    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    // 1. ENDPOINT: Generate Razorpay Order
    @PostMapping("/internal/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, String> payload,
                                         @RequestHeader("X-Internal-Secret") String secretHeader) {

        // Internal Security Check
        if (secretHeader == null || !secretHeader.equals(internalApiSecret)) {
            log.warn("Blocked unauthorized vault access attempt");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
        }

        try {
            String courseId = payload.get("course_id");
            String enrollmentId = payload.get("enrollment_id");
            String userId = payload.get("user_id");

            // Delegate to Service Layer (Returns both order_id and amount dynamically)
            Map<String, Object> orderDetails = checkoutService.createOrder(courseId, enrollmentId, userId);

            return ResponseEntity.ok(orderDetails);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid course requested: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Razorpay Order Creation Failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment gateway error.");
        }
    }

    // 2. ENDPOINT: Verify Payment Signature (CRITICAL FOR RAZORPAY)
    @PostMapping("/internal/verify-signature")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload,
                                           @RequestHeader("X-Internal-Secret") String secretHeader) {

        // Internal Security Check
        if (secretHeader == null || !secretHeader.equals(internalApiSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
        }

        try {
            String orderId = payload.get("razorpay_order_id");
            String paymentId = payload.get("razorpay_payment_id");
            String signature = payload.get("razorpay_signature");

            // Delegate verification math to the Service Layer
            boolean isValid = checkoutService.verifySignature(orderId, paymentId, signature);

            if (isValid) {
                log.info("Payment successfully verified for Order: {}", orderId);
                // The frontend relies on this "verified" status string
                return ResponseEntity.ok(Map.of("status", "verified"));
            } else {
                log.error("PAYMENT SIGNATURE MISMATCH for Order: {}", orderId);
                return ResponseEntity.badRequest().body(Map.of("status", "failed"));
            }
        } catch (Exception e) {
            log.error("Error during payment verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Verification error.");
        }
    }
}