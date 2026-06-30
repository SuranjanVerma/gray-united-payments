package com.grayunited.payments.repository;

import com.grayunited.payments.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    // JpaRepository provides all standard CRUD operations automatically.
}