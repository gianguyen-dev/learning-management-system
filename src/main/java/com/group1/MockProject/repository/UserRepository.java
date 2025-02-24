package com.group1.MockProject.repository;

import com.group1.MockProject.entity.User;
import com.group1.MockProject.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndProvider(String email, String provider);
    Optional<User> findByIdAndRole(int id, UserRole role);
    long countByRole(UserRole role);
    long countByCreatedDateAfter(LocalDateTime date);
}
