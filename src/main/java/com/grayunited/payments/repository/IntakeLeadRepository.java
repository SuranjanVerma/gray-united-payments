package com.grayunited.payments.repository;

import com.grayunited.payments.entity.IntakeLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntakeLeadRepository extends JpaRepository<IntakeLead, Long> {
}