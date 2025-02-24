package com.group1.MockProject.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import com.group1.MockProject.dto.request.CourseRequest;
import com.group1.MockProject.dto.response.CourseDTO;
import com.group1.MockProject.dto.response.InstructorDTO;
import com.group1.MockProject.dto.response.RejectCourseResponse;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.CategoryRepository;
import com.group1.MockProject.repository.CourseRepository;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.utils.JwtUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

  @Mock private ModelMapper modelMapper;

  @Mock private UserRepository userRepository;

  @Mock private CourseRepository courseRepository;

  @Mock private CategoryRepository categoryRepository;

  @Mock private InstructorRepository instructorRepository;

  @InjectMocks private CourseServiceImpl courseService;

  private CourseRequest courseRequest;
  private User mockUser;
  private Instructor mockInstructor;
  private User mockUserIsInstructor;
  private Category mockCategory;
  private Course mockCourse;

  @BeforeEach
  void setUp() {
    mockUserIsInstructor = new User();
    mockUserIsInstructor.setId(1);
    mockUserIsInstructor.setEmail("mockInstructor@email.com");
    mockUserIsInstructor.setPassword("password");
    mockUserIsInstructor.setFullName("Instructor");
    mockUserIsInstructor.setStatus(1);
    mockUserIsInstructor.setRole(UserRole.INSTRUCTOR);

    mockInstructor = new Instructor();
    mockInstructor.setId(1);
    mockInstructor.setUser(mockUserIsInstructor);
    mockInstructor.setName("Instructor");
    mockInstructor.setExpertise("IT");
    mockUserIsInstructor.setInstructor(mockInstructor);

    mockUser = new User();
    mockUser.setId(2);
    mockUser.setEmail("email@email.com");
    mockUser.setPassword("password");
    mockUser.setFullName("FullName");
    mockUser.setStatus(1);
    mockUser.setRole(UserRole.STUDENT);

    SubCategory mockSubCategory = new SubCategory();
    mockSubCategory.setId(1);
    mockSubCategory.setName("SubCategory");
    mockSubCategory.setDescription("SubCategory");

    mockCategory = new Category();
    mockCategory.setId(1);
    mockCategory.setName("Category");
    mockCategory.setDescription("Category");
    mockCategory.setSubCategories(List.of(mockSubCategory));
    mockSubCategory.setCategory(mockCategory);

    courseRequest = new CourseRequest();
    courseRequest.setTitle("Title");
    courseRequest.setDescription("Description");
    courseRequest.setCategoryId(1);
    courseRequest.setInstructorId(1);
    courseRequest.setPrice(50000.0);

    mockCourse = new Course();
    mockCourse.setTitle(courseRequest.getTitle());
    mockCourse.setDescription(courseRequest.getDescription());
    mockCourse.setPrice(courseRequest.getPrice());
    mockCourse.setCategory(mockCategory);
    mockCourse.setInstructor(mockUserIsInstructor.getInstructor());
    mockCourse.setEnrollment(List.of());
  }

  @Test
  public void testCreateCourse_Success() {
    String mockToken = "mockToken";
    String mockInstructorEmail = "mockInstructor@email.com";

    CourseDTO mockCourseDTO = new CourseDTO();
    mockCourseDTO.setTitle(courseRequest.getTitle());

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockInstructorEmail);
      Mockito.when(userRepository.findByEmail(Mockito.eq(mockInstructorEmail)))
          .thenReturn(Optional.of(mockUserIsInstructor));
      Mockito.when(categoryRepository.findById(Mockito.eq(courseRequest.getCategoryId())))
          .thenReturn(Optional.of(mockCategory));
      Mockito.when(courseRepository.save(Mockito.any(Course.class))).thenReturn(mockCourse);
      Mockito.when(modelMapper.map(mockCourse, CourseDTO.class)).thenReturn(mockCourseDTO);

      CourseDTO result = courseService.createCourse(courseRequest, mockToken);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(courseRequest.getTitle(), result.getTitle());
      Mockito.verify(userRepository).findByEmail(Mockito.eq(mockInstructorEmail));
      Mockito.verify(categoryRepository).findById(courseRequest.getCategoryId());
      Mockito.verify(courseRepository).save(Mockito.any(Course.class));
    }
  }

  @Test
  public void testCreateCourse_InvalidToken() {
    String mockToken = "invalidMockToken";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenThrow(new BadCredentialsException("Token đã hết hạn hoặc không hợp lệ"));

      Exception exception =
          assertThrows(
              BadCredentialsException.class,
              () -> courseService.createCourse(courseRequest, mockToken));
      Assertions.assertEquals("Token đã hết hạn hoặc không hợp lệ", exception.getMessage());
    }
  }

  @Test
  public void testCreateCourse_UserNotFound() {
    String mockToken = "mockToken";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn("notfound@email.com");

      Exception exception =
          assertThrows(
              EmptyResultDataAccessException.class,
              () -> courseService.createCourse(courseRequest, mockToken));
      Assertions.assertEquals("Không tìm thấy người dùng", exception.getMessage());
    }
  }

  @Test
  public void testCreateCourse_CategoryNotFound() {
    String mockToken = "mockToken";
    String mockInstructorEmail = "mockInstructor@email.com";

    Course mockCourse = new Course();
    mockCourse.setTitle(courseRequest.getTitle());
    mockCourse.setDescription(courseRequest.getDescription());
    mockCourse.setPrice(courseRequest.getPrice());
    mockCourse.setCategory(mockCategory);
    mockCourse.setInstructor(mockUserIsInstructor.getInstructor());

    CourseDTO mockCourseDTO = new CourseDTO();
    mockCourseDTO.setTitle(courseRequest.getTitle());

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockInstructorEmail);
      Mockito.when(userRepository.findByEmail(Mockito.eq(mockInstructorEmail)))
          .thenReturn(Optional.of(mockUserIsInstructor));
      Mockito.when(categoryRepository.findById(Mockito.eq(courseRequest.getCategoryId())))
          .thenReturn(Optional.empty());

      Exception exception =
          assertThrows(
              EmptyResultDataAccessException.class,
              () -> courseService.createCourse(courseRequest, mockToken));
      Assertions.assertEquals("Không tìm thấy danh mục", exception.getMessage());
    }
  }

  @Test
  public void testUpdateCourse_Success() {
    String mockToken = "mockToken";
    String mockInstructorEmail = "mockInstructor@email.com";

    CourseDTO mockCourseDTO = new CourseDTO();
    mockCourseDTO.setTitle(courseRequest.getTitle());
    mockCourseDTO.setDescription(courseRequest.getDescription());
    mockCourseDTO.setPrice(courseRequest.getPrice());

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockInstructorEmail);
      Mockito.when(courseRepository.findById(Mockito.eq(mockCourse.getId())))
          .thenReturn(Optional.of(mockCourse));
      Mockito.when(userRepository.findByEmail(Mockito.eq(mockInstructorEmail)))
          .thenReturn(Optional.of(mockUserIsInstructor));
      Mockito.when(categoryRepository.findById(Mockito.eq(courseRequest.getCategoryId())))
          .thenReturn(Optional.of(mockCategory));
      Mockito.when(courseRepository.save(Mockito.any(Course.class))).thenReturn(mockCourse);
      Mockito.when(modelMapper.map(mockCourse, CourseDTO.class)).thenReturn(mockCourseDTO);

      CourseDTO result = courseService.updateCourse(mockCourse.getId(), courseRequest, mockToken);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(courseRequest.getTitle(), result.getTitle());
      Assertions.assertEquals(courseRequest.getDescription(), result.getDescription());
      Assertions.assertEquals(courseRequest.getPrice(), result.getPrice());
      Mockito.verify(userRepository).findByEmail(Mockito.eq(mockInstructorEmail));
      Mockito.verify(categoryRepository).findById(courseRequest.getCategoryId());
      Mockito.verify(courseRepository).save(Mockito.any(Course.class));
    }
  }

  @Test
  public void testUpdateCourse_DoNotHaveAccess() {
    String mockToken = "mockToken";
    String mockInstructorEmail = "mockInstructor@email.com";

    CourseDTO mockCourseDTO = new CourseDTO();
    mockCourseDTO.setTitle(courseRequest.getTitle());

    mockUserIsInstructor.setId(3);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockInstructorEmail);
      Mockito.when(courseRepository.findById(Mockito.eq(mockCourse.getId())))
          .thenReturn(Optional.of(mockCourse));
      Mockito.when(userRepository.findByEmail(Mockito.eq(mockInstructorEmail)))
          .thenReturn(Optional.of(mockUser));

      Exception exception =
          Assertions.assertThrows(
              AccessDeniedException.class,
              () -> courseService.updateCourse(mockCourse.getId(), courseRequest, mockToken));
      Assertions.assertEquals(
          "Bạn không có quyền để cập nhật khoá học này", exception.getMessage());
    }
  }

  @Test
  public void testGetCoursesByInstructor_Success() {
    String mockToken = "mockToken";
    String mockEmail = mockUserIsInstructor.getEmail();

    List<Course> mockCourses = List.of(mockCourse);
    InstructorDTO instructorDTO = new InstructorDTO();

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
          .thenReturn(Optional.of(mockUserIsInstructor));
      Mockito.when(
              courseRepository.findByInstructor(Mockito.eq(mockUserIsInstructor.getInstructor())))
          .thenReturn(mockCourses);
      Mockito.when(modelMapper.map(mockCourse.getInstructor(), InstructorDTO.class))
          .thenReturn(instructorDTO);

      List<CourseDTO> result = courseService.getCoursesByInstructor(mockToken);

      Assertions.assertNotNull(result);
    }
  }

  @Test
  public void testGetCoursesByInstructor_NotHaveAccess() {
    String mockToken = "mockToken";
    String mockEmail = mockUser.getEmail();

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
          .thenReturn(Optional.of(mockUser));

      Exception exception =
          Assertions.assertThrows(
              AccessDeniedException.class, () -> courseService.getCoursesByInstructor(mockToken));
      Assertions.assertEquals("Bạn không có quyền xem khoá học này", exception.getMessage());
    }
  }

  @Test
  public void testGetCourseById_Success() {
    int courseId = 1;
    mockCourse.setId(courseId);
    mockCourse.setStatus(2);

    CourseDTO mockCourseDTO = new CourseDTO();
    mockCourseDTO.setTitle("Mock Course Title");

    Mockito.when(courseRepository.findById(Mockito.eq(courseId)))
        .thenReturn(Optional.of(mockCourse));
    Mockito.when(modelMapper.map(mockCourse, CourseDTO.class)).thenReturn(mockCourseDTO);

    CourseDTO result = courseService.getCourseById(courseId);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(mockCourseDTO.getTitle(), result.getTitle());
  }

  @Test
  public void testDeleteCourse_Success() {
    String mockToken = "mockToken";
    String mockInstructorEmail = "mockInstructor@email.com";

    CourseDTO mockCourseDTO = new CourseDTO();
    mockCourseDTO.setTitle(courseRequest.getTitle());

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockInstructorEmail);
      Mockito.when(courseRepository.findById(Mockito.eq(mockCourse.getId())))
          .thenReturn(Optional.of(mockCourse));
      Mockito.when(userRepository.findByEmail(Mockito.eq(mockInstructorEmail)))
          .thenReturn(Optional.of(mockUserIsInstructor));

      courseService.deleteCourse(mockCourse.getId(), mockToken);

      Mockito.verify(courseRepository, Mockito.times(1)).delete(Mockito.eq(mockCourse));
    }
  }

  @Test
  public void testDeleteCourse_DoNotHaveAccess() {
    String mockToken = "mockToken";
    String mockInstructorEmail = "mockInstructor@email.com";

    CourseDTO mockCourseDTO = new CourseDTO();
    mockCourseDTO.setTitle(courseRequest.getTitle());

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockInstructorEmail);
      Mockito.when(courseRepository.findById(Mockito.eq(mockCourse.getId())))
          .thenReturn(Optional.of(mockCourse));
      Mockito.when(userRepository.findByEmail(Mockito.eq(mockInstructorEmail)))
          .thenReturn(Optional.of(mockUser));

      Exception exception =
          Assertions.assertThrows(
              AccessDeniedException.class,
              () -> courseService.deleteCourse(mockCourse.getId(), mockToken));

      Assertions.assertEquals("Bạn không có quyền để xoá khoá học này", exception.getMessage());
    }
  }

  @Test
  public void testDeleteCourse_WhileStudentsInCourse() {
    String mockToken = "mockToken";
    String mockInstructorEmail = "mockInstructor@email.com";

    CourseDTO mockCourseDTO = new CourseDTO();
    mockCourseDTO.setTitle(courseRequest.getTitle());

    Enrollment mockEnrollment = new Enrollment();
    mockEnrollment.setId(1);
    mockEnrollment.setCourse(mockCourse);

    Student mockStudent = new Student();
    mockStudent.setId(2);
    mockStudent.setUser(mockUser);

    mockEnrollment.setStudent(mockStudent);

    mockCourse.setEnrollment(List.of(mockEnrollment));

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockInstructorEmail);
      Mockito.when(courseRepository.findById(Mockito.eq(mockCourse.getId())))
          .thenReturn(Optional.of(mockCourse));
      Mockito.when(userRepository.findByEmail(Mockito.eq(mockInstructorEmail)))
          .thenReturn(Optional.of(mockUserIsInstructor));

      Exception exception =
          Assertions.assertThrows(
              DataIntegrityViolationException.class,
              () -> courseService.deleteCourse(mockCourse.getId(), mockToken));

      Assertions.assertEquals(
          "Không thể xoá khoá học khi còn học viên đang tham gia", exception.getMessage());
    }
  }

  @Test
  public void testUpdateCourseStatus_Success() {
    Mockito.when(courseRepository.findById(Mockito.eq(mockCourse.getId())))
        .thenReturn(Optional.of(mockCourse));

    courseService.updateCourseStatus(mockCourse.getId(), CourseStatus.APPROVED);

    Mockito.verify(courseRepository, Mockito.times(1)).findById(Mockito.eq(mockCourse.getId()));
    Mockito.verify(courseRepository, Mockito.times(1)).save(Mockito.eq(mockCourse));
  }

  @Test
  public void testUpdateCourseStatus_Reject() {
    String reason = "Reject";

    Mockito.when(courseRepository.findById(Mockito.eq(mockCourse.getId())))
        .thenReturn(Optional.of(mockCourse));

    courseService.updateCourseStatus(mockCourse.getId(), CourseStatus.REJECTED, reason);

    Assertions.assertEquals(reason, mockCourse.getRejectionReason());
    Mockito.verify(courseRepository, Mockito.times(1)).findById(Mockito.eq(mockCourse.getId()));
    Mockito.verify(courseRepository, Mockito.times(1)).save(Mockito.eq(mockCourse));
  }

  @Test
  public void testViewAllRejectedCoursesByInstructor() {
    int instructorId = 1;

    Course rejectCourse = new Course();
    rejectCourse.setId(2);
    rejectCourse.setStatus(2);

    List<Course> rejectCourses = List.of(rejectCourse);

    Mockito.when(courseRepository.findByInstructorId(Mockito.eq(instructorId)))
        .thenReturn(rejectCourses);

    List<RejectCourseResponse> result =
        courseService.viewAllRejectedCoursesByInstructor(instructorId);

    Assertions.assertNotNull(result);

    System.out.println(result);
  }

  @Test
  public void testResubmitCourse_Success() {
    String mockToken = "mockToken";
    String mockEmail = "mockInstructor@email.com";
    int courseId = 1;
    int categoryId = 1;

    CourseRequest mockCourseRequest = new CourseRequest();
    mockCourseRequest.setCategoryId(categoryId);
    CourseDTO mockCourseDTO = new CourseDTO();

    mockCourse.setInstructor(mockUserIsInstructor.getInstructor());

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockEmail);
      Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
          .thenReturn(Optional.of(mockUserIsInstructor));
      Mockito.when(courseRepository.findById(Mockito.eq(courseId)))
          .thenReturn(Optional.of(mockCourse));
      Mockito.when(categoryRepository.findById(Mockito.eq(mockCourseRequest.getCategoryId())))
          .thenReturn(Optional.of(mockCategory));
      Mockito.when(courseRepository.save(Mockito.any(Course.class))).thenReturn(mockCourse);
      Mockito.when(modelMapper.map(mockCourse, CourseDTO.class)).thenReturn(mockCourseDTO);

      CourseDTO result = courseService.reSubmitCourse(mockCourseRequest, mockToken, courseId);

      Assertions.assertNotNull(result);
    }
  }

  @Test
  public void testResubmitCourse_NotHaveAccess() {
    String mockToken = "mockToken";
    String mockEmail = "mockInstructor@email.com";
    int courseId = 1;
    int categoryId = 1;

    Instructor mockInstructor = new Instructor();
    mockInstructor.setId(2);
    mockInstructor.setUser(mockUser);

    CourseRequest mockCourseRequest = new CourseRequest();
    mockCourseRequest.setCategoryId(categoryId);
    mockCourse.setInstructor(mockInstructor);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(mockEmail);
      Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
          .thenReturn(Optional.of(mockUserIsInstructor));
      Mockito.when(courseRepository.findById(Mockito.eq(courseId)))
          .thenReturn(Optional.of(mockCourse));

      Exception exception =
          Assertions.assertThrows(
              AccessDeniedException.class,
              () -> courseService.reSubmitCourse(mockCourseRequest, mockToken, courseId));

      Assertions.assertEquals(
          "Bạn không có quyền để cập nhật khoá học này", exception.getMessage());
    }
  }
}
