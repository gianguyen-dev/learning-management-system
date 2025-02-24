package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    Optional<Enrollment> findByCourse(Course course);
}
