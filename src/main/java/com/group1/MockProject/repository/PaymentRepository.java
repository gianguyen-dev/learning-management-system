package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Payment;
import com.group1.MockProject.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
  Optional<Payment> findById(Integer findById);

  Optional<Payment> findByStatus(Integer i);

  Payment findByStudentAndStatus(Student student, int i);

  Optional<Payment> findByPaymentCode(String paymentCode);

  Payment findByStudent(Student student);
}
