package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Analytic;
import com.group1.MockProject.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalyticRepository extends JpaRepository<Analytic, Integer> {

    Optional<Analytic> getByInstructor(Instructor instructor);
}
