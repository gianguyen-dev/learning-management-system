package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Subscription;
import com.group1.MockProject.entity.Student;
import com.group1.MockProject.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    List<Subscription> findByStudent(Student student);
    List<Subscription> findByInstructor(Instructor instructor);
    Optional<Subscription> findByStudentAndInstructor(Student student, Instructor instructor);
}

