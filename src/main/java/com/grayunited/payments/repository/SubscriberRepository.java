package com.grayunited.payments.repository;

import com.grayunited.payments.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// CHANGED: Long to String to match the UUID primary key in the Subscriber entity
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {

    // Allows checking for existence
    boolean existsByEmail(String email);

    // Allows retrieving the actual subscriber record (used in your Controller)
    Optional<Subscriber> findByEmail(String email);
}