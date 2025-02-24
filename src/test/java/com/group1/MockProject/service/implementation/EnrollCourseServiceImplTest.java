package com.group1.MockProject.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Enrollment;
import com.group1.MockProject.entity.Student;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.repository.EnrollCourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnrollCourseServiceImplTest {
  @Mock private EnrollCourseRepository enrollCourseRepository;

  @InjectMocks private EnrollCourseServiceImpl enrollCourseService;

  private Course mockCourse;
  private Student mockStudent;

  @BeforeEach
  void setUp() {
    User mockUser = new User();
    mockUser.setEmail("email@email.com");

    mockStudent = new Student();
    mockStudent.setUser(mockUser);
    mockUser.setStudent(mockStudent);

    mockCourse = new Course();
    mockCourse.setTitle("");
    mockCourse.setDescription("description");
  }

  @Test
  public void testEnrollCourse_Success() {
    Enrollment enrollment = new Enrollment();
    enrollment.setCourse(mockCourse);
    enrollment.setStudent(mockStudent);

    Mockito.when(enrollCourseRepository.save(Mockito.any(Enrollment.class))).thenReturn(enrollment);

    String result = enrollCourseService.addEnroll(mockStudent, mockCourse);

    assertNotNull(result);
    assertEquals("Đăng ký thành công", result);
  }
}
