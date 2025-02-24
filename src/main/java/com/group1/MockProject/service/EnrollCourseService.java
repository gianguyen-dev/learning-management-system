package com.group1.MockProject.service;

import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Student;

public interface EnrollCourseService {
    String addEnroll(Student student, Course course);
}
