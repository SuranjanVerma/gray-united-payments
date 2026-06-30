package com.grayunited.payments.controller;

import com.grayunited.payments.dto.ChannelRequest;
import com.grayunited.payments.service.IntakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/intake")
public class SecureIntakeController {

    private static final Logger logger = LoggerFactory.getLogger(SecureIntakeController.class);
    private final IntakeService intakeService;

    public SecureIntakeController(IntakeService intakeService) {
        this.intakeService = intakeService;
    }

    @PostMapping("/channel")
    public ResponseEntity<ApiResponse> openSecureChannel(@Validated @RequestBody ChannelRequest request) {

        logger.info("Secure channel request initiated for email: {}", request.getWorkEmail());

        try {
            intakeService.processSecureChannel(request);
            return ResponseEntity.ok(new ApiResponse("success", "Secure channel established."));
        } catch (Exception e) {
            logger.error("Failed to establish secure channel", e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("error", "Internal pipeline failure."));
        }
    }

    public record ApiResponse(String status, String message) {}
}