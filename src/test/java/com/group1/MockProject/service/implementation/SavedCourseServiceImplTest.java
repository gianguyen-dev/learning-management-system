package com.group1.MockProject.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import com.group1.MockProject.dto.response.GetSavedCourseResponse;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.SavedCourseRepository;
import com.group1.MockProject.repository.StudentRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.utils.JwtUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class SavedCourseServiceImplTest {
  @Mock private StudentRepository studentRepository;

  @Mock private SavedCourseRepository savedCourseRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private SavedCourseServiceImpl savedCourseService;

  private User mockUser;
  private Student mockStudent;
  private SavedCourse mockSavedCourse;

  @BeforeEach
  void setUp() {
    mockUser = new User();
    mockUser.setId(1);
    mockUser.setEmail("mock@email.com");
    mockUser.setRole(UserRole.STUDENT);
    mockUser.setFullName("Mock User");
    mockUser.setStatus(1);

    mockStudent = new Student();
    mockStudent.setId(1);
    mockStudent.setStudentCode("520H0374");
    mockStudent.setUser(mockUser);
    mockUser.setStudent(mockStudent);

    Course mockCourse = new Course();
    mockCourse.setId(1);
    mockCourse.setTitle("Mock Course");
    mockCourse.setDescription("Mock Course");

    mockSavedCourse = new SavedCourse();
    mockSavedCourse.setId(1);
    mockSavedCourse.setCourse(mockCourse);
    mockSavedCourse.setStudent(mockStudent);
  }

  @Test
  public void testGetSavedCoursesByToken_Success() {
    String mockToken = "mockToken";
    String mockEmail = "mock@email.com";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken))).thenReturn(mockEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
          .thenReturn(Optional.of(mockUser));

      Mockito.when(savedCourseRepository.findByStudent(Mockito.any(Student.class)))
          .thenReturn(List.of(mockSavedCourse));

      GetSavedCourseResponse result = savedCourseService.getSavedCoursesByEmail(mockEmail);

      Assertions.assertNotNull(result);
      Assertions.assertEquals("Lấy danh sách khóa học thành công", result.getMessage());
    }
  }

  @Test
  public void testGetSavedCoursesByToken_UserNotFound() {
    String mockToken = "mockToken";
    String mockEmail = "mock@email.com";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail))).thenReturn(Optional.empty());

      Exception exception =
          Assertions.assertThrows(
              EmptyResultDataAccessException.class,
              () -> savedCourseService.getSavedCoursesByEmail(mockEmail));

      Assertions.assertEquals("Không tìm thấy người dùng", exception.getMessage());
    }
  }

  @Test
  public void testGetSavedCoursesByToken_DoNotHaveAccess() {
    String mockToken = "mockToken";
    String mockEmail = "mock@email.com";

    mockUser.setStudent(null);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
          .thenReturn(Optional.of(mockUser));

      Exception exception =
          Assertions.assertThrows(
              AccessDeniedException.class,
              () -> savedCourseService.getSavedCoursesByEmail(mockEmail));

      Assertions.assertEquals("Bạn không có quyền đăng kí khóa học", exception.getMessage());
    }
  }

  @Test
  public void testGetSavedCoursesByToken_SavedCourseNotFound() {
    String mockToken = "mockToken";
    String mockEmail = "mock@email.com";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
          .thenReturn(Optional.of(mockUser));
      Mockito.when(savedCourseRepository.findByStudent(Mockito.any(Student.class)))
          .thenReturn(List.of());

      Exception exception =
          Assertions.assertThrows(
              EmptyResultDataAccessException.class,
              () -> savedCourseService.getSavedCoursesByEmail(mockEmail));

      Assertions.assertEquals("Không tìm thấy khóa học đã lưu", exception.getMessage());
    }
  }
}
