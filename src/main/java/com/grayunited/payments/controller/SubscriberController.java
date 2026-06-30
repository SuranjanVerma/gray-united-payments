package com.grayunited.payments.controller;

import com.grayunited.payments.dto.SubscribeRequest;
import com.grayunited.payments.service.SubscriberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscribers")
// @CrossOrigin IS REMOVED. Handled by SecurityConfig.java
public class SubscriberController {

    private final SubscriberService subscriberService;

    // Constructor Injection
    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/newsletter")
    public ResponseEntity<Map<String, Object>> subscribe(@Valid @RequestBody SubscribeRequest request) {

        // Note: Because we used @Valid, Spring automatically blocks bad emails
        // before this code even runs and returns a 400 Bad Request.

        String message = subscriberService.subscribe(request.getEmail());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", message
        ));
    }
}