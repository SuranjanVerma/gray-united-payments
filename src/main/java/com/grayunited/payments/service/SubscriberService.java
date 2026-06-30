package com.grayunited.payments.service;

import com.grayunited.payments.entity.Subscriber;
import com.grayunited.payments.repository.SubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SubscriberService {

    private static final Logger log = LoggerFactory.getLogger(SubscriberService.class);
    private final SubscriberRepository subscriberRepository;

    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public String subscribe(String email) {
        if (subscriberRepository.existsByEmail(email)) {
            return "You are already subscribed!";
        }

        Subscriber subscriber = new Subscriber(email);
        subscriberRepository.save(subscriber);
        log.info("📩 New subscriber: {}", email);

        return "Subscribed successfully";
    }
}