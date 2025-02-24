package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.response.*;
import com.group1.MockProject.entity.Category;
import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Student;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.repository.*;
import com.group1.MockProject.service.HomePageService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HomePageServiceImpl implements HomePageService {

  private final UserRepository userRepository;
  private StudentRepository studentRepository;
  private InstructorRepository instructorRepository;
  private CourseRepository courseRepository;
  private CategoryRepository categoryRepository;
  private CartRepository cartRepository;
  private SubcriptionRepository subcriptionRepository;
  private SavedCourseRepository savedCourseRepository;
  private NotificationRepository notificationRepository;
  private EnrollmentRepository enrollmentRepository;
  private ModelMapper mapper;

  public HomePageServiceImpl(
      StudentRepository studentRepository,
      InstructorRepository instructorRepository,
      CourseRepository courseRepository,
      CategoryRepository categoryRepository,
      CartRepository cartRepository,
      SubcriptionRepository subcriptionRepository,
      SavedCourseRepository savedCourseRepository,
      NotificationRepository notificationRepository,
      EnrollmentRepository enrollmentRepository,
      ModelMapper mapper,
      UserRepository userRepository) {

    this.studentRepository = studentRepository;
    this.instructorRepository = instructorRepository;
    this.courseRepository = courseRepository;
    this.categoryRepository = categoryRepository;
    this.cartRepository = cartRepository;
    this.subcriptionRepository = subcriptionRepository;
    this.savedCourseRepository = savedCourseRepository;
    this.notificationRepository = notificationRepository;
    this.enrollmentRepository = enrollmentRepository;
    this.mapper = mapper;
    this.userRepository = userRepository;
  }

  @Override
  public StudentHomePageDTO getHomePageForStudent(String email) {
    // Create response
    StudentHomePageDTO studentHomePageDTO = new StudentHomePageDTO();

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại trong hệ thống"));

    Student student = user.getStudent();

    // Get all courses and map to coursedtos
    List<CourseDTO> courseDTOs =
        courseRepository.findAll().stream()
            .map(course -> mapper.map(course, CourseDTO.class))
            .toList();
    studentHomePageDTO.setCourseDTOs(courseDTOs);

    // Get all categories and map to categorydtos
    List<CategoryDTO> categoryDTOs =
        categoryRepository.findAll().stream()
            .map(category -> mapper.map(category, CategoryDTO.class))
            .toList();
    studentHomePageDTO.setCategoryDTOs(categoryDTOs);

    // Get cart from student and map to dto
    CartDTO cartDTOs =
        student.getCart() != null ? mapper.map(student.getCart(), CartDTO.class) : new CartDTO();
    studentHomePageDTO.setCartDTOs(cartDTOs);

    // Get Subcription from student and map to dto
    List<SubcriptionDTO> subcriptionDTOs =
        student.getSubscriptions() != null
            ? student.getSubscriptions().stream()
                .map(sub -> mapper.map(sub, SubcriptionDTO.class))
                .toList()
            : new ArrayList<>();
    studentHomePageDTO.setSubcriptionDTOs(subcriptionDTOs);

    // Get saved course from student and map to dto
    List<SavedCourseDTO> savedCourseDTOs =
        student.getSavedCourse() != null
            ? student.getSavedCourse().stream()
                .map(savedCourse -> mapper.map(savedCourse, SavedCourseDTO.class))
                .toList()
            : new ArrayList<>();
    studentHomePageDTO.setSavedCourseDTOs(savedCourseDTOs);

    // Get notification from student and map to dto
    List<NotificationDTO> notificationDTOs =
        student.getNotifications() != null
            ? student.getNotifications().stream()
                .map(notification -> mapper.map(notification, NotificationDTO.class))
                .toList()
            : new ArrayList<>();
    studentHomePageDTO.setNotificationDTOs(notificationDTOs);

    // Get enrollment courses from student and map to dto
    List<EnrollmentDTO> enrollmentDTOs =
        student.getEnrollments() != null
            ? student.getEnrollments().stream()
                .map(enrollment -> mapper.map(enrollment, EnrollmentDTO.class))
                .toList()
            : new ArrayList<>();
    studentHomePageDTO.setEnrollmentDTOs(enrollmentDTOs);

    return studentHomePageDTO;
  }

  @Override
  public GuestHomePageDTO getHomePageForGuest() {
    GuestHomePageDTO guestHomePageDTO = new GuestHomePageDTO();

    // Get all courses and map to coursedtos
    List<CourseDTO> courseDTOs =
        courseRepository.findAll().stream()
            .map(course -> mapper.map(course, CourseDTO.class))
            .toList();
    guestHomePageDTO.setCourse(courseDTOs);

    // Get all categories and map to categorydtos
    List<CategoryDTO> categoryDTOs =
        categoryRepository.findAll().stream()
            .map(category -> mapper.map(category, CategoryDTO.class))
            .toList();
    guestHomePageDTO.setCategories(categoryDTOs);

    return guestHomePageDTO;
  }
}
