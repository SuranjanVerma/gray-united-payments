package com.grayunited.payments.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "intake_leads")
@Data
public class IntakeLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String workEmail;
    private String company;
    private String contractSize;

    @Column(columnDefinition = "TEXT")
    private String brief;

    private LocalDateTime submittedAt = LocalDateTime.now();
}