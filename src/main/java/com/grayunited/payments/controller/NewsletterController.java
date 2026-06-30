package com.grayunited.payments.controller;

import com.grayunited.payments.dto.SubscribeRequest;
import com.grayunited.payments.entity.Subscriber;
import com.grayunited.payments.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // adjust for production
public class NewsletterController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    private SubscriberRepository subscriberRepository;

    @PostMapping("/newsletter")
    public ResponseEntity<Map<String, Object>> subscribe(@RequestBody SubscribeRequest request) {
        String email = request.getEmail();
        Map<String, Object> response = new HashMap<>();

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            response.put("success", false);
            response.put("message", "Valid email required");
            return ResponseEntity.badRequest().body(response);
        }

        if (subscriberRepository.existsByEmail(email)) {
            response.put("success", true);
            response.put("message", "You are already subscribed!");
            return ResponseEntity.ok(response);
        }

        Subscriber subscriber = new Subscriber(email);
        subscriberRepository.save(subscriber);
        System.out.println("📩 New subscriber: " + email);

        response.put("success", true);
        response.put("message", "Subscribed successfully");
        return ResponseEntity.ok(response);
    }
}