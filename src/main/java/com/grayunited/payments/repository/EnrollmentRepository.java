package com.grayunited.payments.repository;

import com.grayunited.payments.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

    // Updated to match the new field name in Enrollment entity
    Optional<Enrollment> findByRazorpayOrderId(String razorpayOrderId);
}