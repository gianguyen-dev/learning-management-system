package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.response.InstructorResponse;
import com.group1.MockProject.dto.response.PageStudentsDTO;
import com.group1.MockProject.dto.response.StudentDTO;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.StudentRepository;
import com.group1.MockProject.repository.SubscriptionRepository;
import com.group1.MockProject.repository.UserRepository;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

  @Mock private StudentRepository studentRepository;

  @Mock private SubscriptionRepository subscriptionRepository;

  @Mock private InstructorRepository instructorRepository;

  @Mock private UserRepository userRepository;

  @Mock private ModelMapper mapper;

  @InjectMocks private StudentServiceImpl studentService;

  private Student mockStudent;
  private User mockUser;
  private User mockUserIsInstructor;
  private Instructor mockInstructor;
  private Course mockCourse;
  private Subscription mockSubscription;

  @BeforeEach
  void setUp() {
    mockUser = new User();
    mockUser.setId(1);
    mockUser.setEmail("mock@email.com");
    mockUser.setRole(UserRole.STUDENT);
    mockUser.setFullName("Mock User");
    mockUser.setStatus(1);

    mockUserIsInstructor = new User();
    mockUserIsInstructor.setId(2);
    mockUserIsInstructor.setEmail("mock@instructor.com");
    mockUserIsInstructor.setRole(UserRole.INSTRUCTOR);
    mockUserIsInstructor.setFullName("Mock Instructor");
    mockUserIsInstructor.setStatus(1);

    mockInstructor = new Instructor();
    mockInstructor.setId(1);
    mockInstructor.setUser(mockUserIsInstructor);
    mockInstructor.setExpertise("IT");

    mockStudent = new Student();
    mockStudent.setId(1);
    mockStudent.setStudentCode("520H0374");
    mockStudent.setUser(mockUser);
    mockUser.setStudent(mockStudent);

    mockSubscription = new Subscription();
    mockSubscription.setId(1);
    mockSubscription.setStudent(mockStudent);
    mockSubscription.setInstructor(mockInstructor);
    mockStudent.setSubscriptions(List.of(mockSubscription));

    mockCourse = new Course();
    mockCourse.setId(1);
    mockCourse.setInstructor(mockInstructor);
    mockCourse.setTitle("Mock Course");
    mockCourse.setDescription("Mock Course");
    mockCourse.setStatus(1);
    mockCourse.setPrice(50000.0);
  }

  @Test
  public void testViewListSubscription_Success() {
    String studentEmail = "mock@email.com";

    Mockito.when(studentRepository.findByUserEmail(studentEmail))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(subscriptionRepository.findByStudent(Mockito.eq(mockStudent)))
        .thenReturn(mockStudent.getSubscriptions());

    List<InstructorResponse> result = studentService.viewListSubscription(studentEmail);

    Assertions.assertNotNull(result);
    Mockito.verify(subscriptionRepository, Mockito.times(1))
        .findByStudent(Mockito.any(Student.class));
  }

  @Test
  public void testViewListSubscription_NoStudentFound() {
    String studentEmail = "invalid@email.com";

    Mockito.when(studentRepository.findByUserEmail(studentEmail)).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> studentService.viewListSubscription(studentEmail));
    Assertions.assertEquals("Không tìm thấy học viên", exception.getMessage());
  }

  @Test
  public void testSearchInstructor_Success() {
    String instructorName = mockInstructor.getName();

    Mockito.when(instructorRepository.findByNameContainingIgnoreCase(Mockito.eq(instructorName)))
        .thenReturn(List.of(mockInstructor));

    List<InstructorResponse> result = studentService.searchInstructor(instructorName);

    Assertions.assertEquals(result.getFirst().getName(), instructorName);
    Mockito.verify(instructorRepository, Mockito.times(1))
        .findByNameContainingIgnoreCase(Mockito.eq(instructorName));
  }

  @Test
  public void testSearchInstructor_NoInstructorFound() {
    String instructorName = mockInstructor.getName();

    Mockito.when(instructorRepository.findByNameContainingIgnoreCase(Mockito.eq(instructorName)))
        .thenReturn(List.of());

    List<InstructorResponse> result = studentService.searchInstructor(instructorName);

    Assertions.assertEquals(List.of(), result);
    Mockito.verify(instructorRepository, Mockito.times(1))
        .findByNameContainingIgnoreCase(Mockito.eq(instructorName));
  }

  @Test
  public void testSubscribeToInstructor_Success() {
    String studentEmail = mockUser.getEmail();
    int instructorId = mockInstructor.getId();

    Mockito.when(userRepository.findByEmail(studentEmail)).thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(instructorRepository.findById(instructorId))
        .thenReturn(Optional.of(mockInstructor));
    Mockito.when(
            subscriptionRepository.findByStudentAndInstructor(
                Mockito.eq(mockStudent), Mockito.eq(mockInstructor)))
        .thenReturn(Optional.empty());

    String result = studentService.subscribeToInstructor(studentEmail, instructorId);

    Assertions.assertEquals("Đăng ký theo dõi giảng viên thành công", result);
    Mockito.verify(subscriptionRepository, Mockito.times(1)).save(Mockito.any(Subscription.class));
  }

  @Test
  public void testSubscribeToInstructor_NoUserFound() {
    String studentEmail = "invalid@email.com";
    int instructorId = mockInstructor.getId();

    Mockito.when(userRepository.findByEmail(studentEmail)).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> studentService.subscribeToInstructor(studentEmail, instructorId));

    Assertions.assertEquals("Không tìm thấy người dùng", exception.getMessage());
  }

  @Test
  public void testSubscribeToInstructor_NoStudentFound() {
    String studentEmail = mockUser.getEmail();
    int instructorId = mockInstructor.getId();

    Mockito.when(userRepository.findByEmail(studentEmail)).thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser))).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> studentService.subscribeToInstructor(studentEmail, instructorId));

    Assertions.assertEquals("Không tìm thấy học viên", exception.getMessage());
  }

  @Test
  public void testSubscribeToInstructor_NoInstructorFound() {
    String studentEmail = mockUser.getEmail();
    int instructorId = mockInstructor.getId();

    Mockito.when(userRepository.findByEmail(studentEmail)).thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> studentService.subscribeToInstructor(studentEmail, instructorId));

    Assertions.assertEquals("Không tìm thấy giảng viên", exception.getMessage());
  }

  @Test
  public void testSubscribeToInstructor_AlreadySubscribed() {
    String studentEmail = mockUser.getEmail();
    int instructorId = mockInstructor.getId();

    Mockito.when(userRepository.findByEmail(studentEmail)).thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(instructorRepository.findById(instructorId))
        .thenReturn(Optional.of(mockInstructor));
    Mockito.when(
            subscriptionRepository.findByStudentAndInstructor(
                Mockito.eq(mockStudent), Mockito.eq(mockInstructor)))
        .thenReturn(Optional.of(mockSubscription));

    Exception exception =
        Assertions.assertThrows(
            DataIntegrityViolationException.class,
            () -> studentService.subscribeToInstructor(studentEmail, instructorId));

    Assertions.assertEquals("Bạn đã đăng ký theo dõi giảng viên này rồi", exception.getMessage());
  }

  @Test
  public void testUnsubscribeToInstructor_Success() {
    String studentEmail = mockUser.getEmail();
    int instructorId = mockInstructor.getId();

    Mockito.when(userRepository.findByEmail(studentEmail)).thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(instructorRepository.findById(instructorId))
        .thenReturn(Optional.of(mockInstructor));
    Mockito.when(
            subscriptionRepository.findByStudentAndInstructor(
                Mockito.eq(mockStudent), Mockito.eq(mockInstructor)))
        .thenReturn(Optional.of(mockSubscription));

    String result = studentService.unsubscribeFromInstructor(studentEmail, instructorId);

    Assertions.assertEquals("Hủy đăng ký theo dõi giảng viên thành công", result);
    Mockito.verify(subscriptionRepository, Mockito.times(1))
        .delete(Mockito.any(Subscription.class));
  }

  @Test
  public void testUnsubscribeToInstructor_NoUserFound() {
    String studentEmail = "invalid@email.com";
    int instructorId = mockInstructor.getId();

    Mockito.when(userRepository.findByEmail(studentEmail)).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> studentService.unsubscribeFromInstructor(studentEmail, instructorId));

    Assertions.assertEquals("Không tìm thấy người dùng", exception.getMessage());
  }

  @Test
  public void testUnsubscribeToInstructor_NoStudentFound() {
    String studentEmail = mockUser.getEmail();
    int instructorId = mockInstructor.getId();

    Mockito.when(userRepository.findByEmail(studentEmail)).thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser))).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> studentService.unsubscribeFromInstructor(studentEmail, instructorId));

    Assertions.assertEquals("Không tìm thấy học viên", exception.getMessage());
  }

  @Test
  public void testUnsubscribeToInstructor_NoInstructorFound() {
    String studentEmail = mockUser.getEmail();
    int instructorId = mockInstructor.getId();

    Mockito.when(userRepository.findByEmail(studentEmail)).thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(instructorRepository.findById(instructorId)).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> studentService.unsubscribeFromInstructor(studentEmail, instructorId));

    Assertions.assertEquals("Không tìm thấy giảng viên", exception.getMessage());
  }

  @Test
  public void testUnsubscribeToInstructor_NotYetSubscribed() {
    String studentEmail = mockUser.getEmail();
    int instructorId = mockInstructor.getId();

    Mockito.when(userRepository.findByEmail(studentEmail)).thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(instructorRepository.findById(instructorId))
        .thenReturn(Optional.of(mockInstructor));
    Mockito.when(
            subscriptionRepository.findByStudentAndInstructor(
                Mockito.eq(mockStudent), Mockito.eq(mockInstructor)))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            DataIntegrityViolationException.class,
            () -> studentService.unsubscribeFromInstructor(studentEmail, instructorId));

    Assertions.assertEquals("Bạn chưa đăng ký theo dõi giảng viên này", exception.getMessage());
  }

  @Test
  public void testGetStudentById_Success() {
    int studentId = mockStudent.getId();

    Mockito.when(studentRepository.findByUserId(Mockito.eq(studentId)))
        .thenReturn(Optional.of(mockStudent));

    Student result = studentService.getStudentByUserId(studentId);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(mockStudent, result);
    Mockito.verify(studentRepository, Mockito.times(1)).findByUserId(Mockito.eq(studentId));
  }

  @Test
  public void testGetStudentById_UserNotFound() {
    int studentId = mockStudent.getId();

    Mockito.when(studentRepository.findByUserId(Mockito.eq(studentId)))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> studentService.getStudentByUserId(studentId));

    Assertions.assertEquals("Không tìm thấy người dùng", exception.getMessage());
  }

  @Test
  public void testGetAllStudent_Success() {
    int pageNo = 1;
    int pageSize = 10;
    String sortBy = "id";
    String sortDir = "asc";

    StudentDTO studentDTO = new StudentDTO();

    Page<Student> studentPage =
        new PageImpl<>(List.of(mockStudent), PageRequest.of(pageNo, pageSize), 1);

    Mockito.when(
            studentRepository.findAll(
                Mockito.any(Specification.class), Mockito.any(Pageable.class)))
        .thenReturn(studentPage);
    Mockito.when(mapper.map(mockUser, StudentDTO.class)).thenReturn(studentDTO);

    PageStudentsDTO result = studentService.getAllStudents(pageNo, pageSize, sortBy, sortDir);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getPageNo());
    Assertions.assertEquals(10, result.getPageSize());
    Assertions.assertEquals(11, result.getTotalElements());
    Assertions.assertEquals(1, result.getContent().size());
  }

  @Test
  public void testGetAllStudent_WithInvalidInputs() {
    int pageNo = -1;
    int pageSize = -1;
    String sortBy = "invalid";
    String sortDir = "invalid";

    StudentDTO studentDTO = new StudentDTO();

    Page<Student> studentPage = new PageImpl<>(List.of(mockStudent), PageRequest.of(0, 10), 1);

    Mockito.when(
            studentRepository.findAll(
                Mockito.any(Specification.class), Mockito.any(Pageable.class)))
        .thenReturn(studentPage);
    Mockito.when(mapper.map(mockUser, StudentDTO.class)).thenReturn(studentDTO);

    PageStudentsDTO result = studentService.getAllStudents(pageNo, pageSize, sortBy, sortDir);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(0, result.getPageNo());
    Assertions.assertEquals(10, result.getPageSize());
    Assertions.assertEquals(1, result.getTotalElements());
    Assertions.assertEquals(1, result.getContent().size());
  }
}
