package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Enrollment;
import com.group1.MockProject.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollCourseRepository extends JpaRepository<Enrollment, Integer> {
    Optional<Enrollment> findByCourseAndStudent(Course course, Student student);
}
