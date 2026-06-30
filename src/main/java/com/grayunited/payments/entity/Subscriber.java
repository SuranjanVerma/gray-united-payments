package com.grayunited.payments.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "subscribers")
@Getter // Applies getters to ALL fields automatically
@Setter // Applies setters to ALL fields automatically
public class Subscriber {

    @Id
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "unsubscribe_token", unique = true, length = 36)
    private String unsubscribeToken;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    // Default constructor required by JPA
    public Subscriber() {}

    // Convenience constructor for programmatic creation
    public Subscriber(String email) {
        this.email = email;
    }

    /**
     * JPA Lifecycle Hook
     * Guarantees fields are safely populated during persistence
     * if they weren't explicitly initialized beforehand.
     */
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.unsubscribeToken == null) {
            this.unsubscribeToken = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = ZonedDateTime.now(ZoneOffset.UTC);
        }
    }
}