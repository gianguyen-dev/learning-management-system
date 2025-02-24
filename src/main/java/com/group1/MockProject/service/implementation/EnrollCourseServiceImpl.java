package com.group1.MockProject.service.implementation;

import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Enrollment;
import com.group1.MockProject.entity.Student;
import com.group1.MockProject.repository.EnrollCourseRepository;
import com.group1.MockProject.service.EnrollCourseService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EnrollCourseServiceImpl implements EnrollCourseService {

    private final EnrollCourseRepository enrollCourseRepository;

    public EnrollCourseServiceImpl(EnrollCourseRepository enrollCourseRepository) {
        this.enrollCourseRepository = enrollCourseRepository;
    }

    @Override
    public String addEnroll(Student student, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());

        enrollCourseRepository.save(enrollment);

        return "Đăng ký thành công";
    }
}
