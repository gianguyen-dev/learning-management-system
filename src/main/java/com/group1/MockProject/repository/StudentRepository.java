package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Student;
import com.group1.MockProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {
    Optional<Student> findByUser(User user);
    Optional<Student> findByUserEmail(String email);

    Optional<Object> findByUserId(int userId);
}
