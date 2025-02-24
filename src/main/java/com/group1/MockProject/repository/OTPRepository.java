package com.group1.MockProject.repository;

import com.group1.MockProject.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByEmail(String email);
}
