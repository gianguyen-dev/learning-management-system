package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.response.CategoryDTO;
import com.group1.MockProject.dto.response.CourseDTO;
import com.group1.MockProject.dto.response.GuestHomePageDTO;
import com.group1.MockProject.dto.response.StudentHomePageDTO;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class HomePageServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private StudentRepository studentRepository;

  @Mock private InstructorRepository instructorRepository;

  @Mock private CourseRepository courseRepository;

  @Mock private CategoryRepository categoryRepository;

  @Mock private CartRepository cartRepository;

  @Mock private SubcriptionRepository subcriptionRepository;

  @Mock private SavedCourseRepository savedCourseRepository;

  @Mock private NotificationRepository notificationRepository;

  @Mock private EnrollmentRepository enrollmentRepository;

  @Mock private ModelMapper mapper;

  @InjectMocks private HomePageServiceImpl homePageService;

  private User mockUser;
  private Student mockStudent;

  @BeforeEach
  void setUp() {
    mockUser = new User();
    mockUser.setEmail("email@email.com");

    mockStudent = new Student();
    mockStudent.setUser(mockUser);
    mockUser.setStudent(mockStudent);
    mockStudent.setCart(new Cart());
    mockStudent.setSubscriptions(List.of(new Subscription()));
    mockStudent.setSavedCourse(List.of(new SavedCourse()));
    mockStudent.setNotifications(List.of(new Notification()));
    mockStudent.setEnrollments(List.of(new Enrollment()));
  }

  @Test
  public void testGetHomePageForStudent_Success() {
    String email = "email@email.com";

    Course mockCourse = new Course();
    CourseDTO courseDTO = new CourseDTO();

    Category mockCategory = new Category();
    CategoryDTO categoryDTO = new CategoryDTO();

    Mockito.when(userRepository.findByEmail(Mockito.eq(email))).thenReturn(Optional.of(mockUser));
    Mockito.when(courseRepository.findAll()).thenReturn(List.of(mockCourse));
    Mockito.when(mapper.map(mockCourse, CourseDTO.class)).thenReturn(courseDTO);
    Mockito.when(categoryRepository.findAll()).thenReturn(List.of(mockCategory));
    Mockito.when(mapper.map(mockCategory, CategoryDTO.class)).thenReturn(categoryDTO);

    StudentHomePageDTO result = homePageService.getHomePageForStudent(email);
    Assertions.assertNotNull(result);
  }

  @Test
  public void testGetHomePageForStudent_NoCartFound() {
    String email = "email@email.com";

    Course mockCourse = new Course();
    CourseDTO courseDTO = new CourseDTO();

    Category mockCategory = new Category();
    CategoryDTO categoryDTO = new CategoryDTO();

    mockStudent.setCart(null);

    Mockito.when(userRepository.findByEmail(Mockito.eq(email))).thenReturn(Optional.of(mockUser));
    Mockito.when(courseRepository.findAll()).thenReturn(List.of(mockCourse));
    Mockito.when(mapper.map(mockCourse, CourseDTO.class)).thenReturn(courseDTO);
    Mockito.when(categoryRepository.findAll()).thenReturn(List.of(mockCategory));
    Mockito.when(mapper.map(mockCategory, CategoryDTO.class)).thenReturn(categoryDTO);

    StudentHomePageDTO result = homePageService.getHomePageForStudent(email);
    Assertions.assertNotNull(result);
  }

  @Test
  public void testGetHomePageForStudent_NoSubscriptionFound() {
    String email = "email@email.com";

    Course mockCourse = new Course();
    CourseDTO courseDTO = new CourseDTO();

    Category mockCategory = new Category();
    CategoryDTO categoryDTO = new CategoryDTO();

    mockStudent.setSubscriptions(null);

    Mockito.when(userRepository.findByEmail(Mockito.eq(email))).thenReturn(Optional.of(mockUser));
    Mockito.when(courseRepository.findAll()).thenReturn(List.of(mockCourse));
    Mockito.when(mapper.map(mockCourse, CourseDTO.class)).thenReturn(courseDTO);
    Mockito.when(categoryRepository.findAll()).thenReturn(List.of(mockCategory));
    Mockito.when(mapper.map(mockCategory, CategoryDTO.class)).thenReturn(categoryDTO);

    StudentHomePageDTO result = homePageService.getHomePageForStudent(email);
    Assertions.assertNotNull(result);
  }

  @Test
  public void testGetHomePageForGuest_Success() {
    Course mockCourse = new Course();
    CourseDTO courseDTO = new CourseDTO();

    Category mockCategory = new Category();
    CategoryDTO categoryDTO = new CategoryDTO();

    Mockito.when(courseRepository.findAll()).thenReturn(List.of(mockCourse));
    Mockito.when(mapper.map(mockCourse, CourseDTO.class)).thenReturn(courseDTO);
    Mockito.when(categoryRepository.findAll()).thenReturn(List.of(mockCategory));
    Mockito.when(mapper.map(mockCategory, CategoryDTO.class)).thenReturn(categoryDTO);

    GuestHomePageDTO result = homePageService.getHomePageForGuest();
    Assertions.assertNotNull(result);
  }
}
