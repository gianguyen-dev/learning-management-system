package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.MyCourse;
import com.group1.MockProject.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyCourseRepository extends JpaRepository<MyCourse, Integer> {
    Optional<MyCourse> findByCourseAndStudent(Course course, Student student);
}
