package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Instructor;
import com.group1.MockProject.entity.SavedCourse;
import com.group1.MockProject.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedCourseRepository extends JpaRepository <SavedCourse, Integer>{
    Optional<SavedCourse> findByCourse(Course course);

    Optional<SavedCourse> findByCourseAndStudent(Course course, Student student);

    List<SavedCourse> findByStudent(Student student);
}
