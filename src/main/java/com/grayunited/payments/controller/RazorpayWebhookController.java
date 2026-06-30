package com.grayunited.payments.controller;

import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class RazorpayWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayWebhookController.class);

    // Note: Webhook secrets are different from API secrets in Razorpay!
    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Value("${python.core.api.url}")
    private String pythonCoreApiUrl;

    @Value("${internal.api.secret}") // Updated to match your application.properties
    private String internalSecret;

    private final RestTemplate restTemplate;

    public RazorpayWebhookController() {
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signatureHeader) {

        try {
            // 1. Securely verify the webhook actually came from Razorpay
            boolean isValid = Utils.verifyWebhookSignature(payload, signatureHeader, webhookSecret);

            if (!isValid) {
                logger.error("Invalid Razorpay Webhook Signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }

            // 2. Parse the Webhook JSON Payload
            JSONObject payloadJson = new JSONObject(payload);
            String event = payloadJson.getString("event");

            // 3. Listen for successful payment captures
            if ("payment.captured".equals(event) || "order.paid".equals(event)) {

                JSONObject paymentEntity = payloadJson.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

                // Extract the custom notes we attached during Order creation
                JSONObject notes = paymentEntity.optJSONObject("notes");

                if (notes != null && notes.has("course_id") && notes.has("user_id")) {
                    // In our createOrder setup, we mapped enrollmentId to the "receipt" field
                    String enrollmentId = paymentEntity.optString("receipt", null);

                    if (enrollmentId != null && !enrollmentId.isEmpty()) {
                        logger.info("Payment confirmed via Webhook for enrollment: {}. Notifying Python Core...", enrollmentId);
                        notifyPythonCore(enrollmentId, "completed");
                    } else {
                        logger.warn("Received successful payment but 'receipt' (enrollment_id) was missing.");
                    }
                }
            }

            return ResponseEntity.ok("Webhook Processed");

        } catch (Exception e) {
            logger.error("Error processing Razorpay webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing error");
        }
    }

    /**
     * Sends a secure internal request to the Python backend to unlock the course.
     */
    private void notifyPythonCore(String enrollmentId, String status) {
        String url = pythonCoreApiUrl + "/api/payments/internal/enrollments/complete";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Secret", internalSecret); // The cryptographic handshake

        Map<String, String> body = new HashMap<>();
        body.put("enrollment_id", enrollmentId);
        body.put("status", status);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Core API successfully updated enrollment {}", enrollmentId);
            } else {
                logger.error("Core API rejected the update for {}. Status: {}", enrollmentId, response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Failed to reach Python Core API to update enrollment {}: {}", enrollmentId, e.getMessage());
        }
    }
}