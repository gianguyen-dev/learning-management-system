package com.group1.MockProject.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import com.group1.MockProject.dto.request.AddPaymentRequest;
import com.group1.MockProject.dto.request.PaymentRequest;
import com.group1.MockProject.dto.response.AddPaymentResponse;
import com.group1.MockProject.dto.response.PaymentResponse;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
  @Mock private PaymentRepository paymentRepository;

  @Mock private CourseRepository courseRepository;

  @Mock private PaymentDetailRepository paymentDetailRepository;

  @Mock private SavedCourseRepository savedCourseRepository;

  @Mock private StudentRepository studentRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private PaymentServiceImpl paymentService;

  private User mockUser;
  private Student mockStudent;
  private Course mockCourse;
  private Payment mockPayment;

  @BeforeEach
  void setUp() {
    mockUser = new User();
    mockUser.setEmail("email@email.com");

    mockStudent = new Student();
    mockStudent.setUser(mockUser);
    mockUser.setStudent(mockStudent);

    mockCourse = new Course();
    mockCourse.setId(1);
    mockCourse.setPrice(50000.0);

    mockPayment = new Payment();
  }

  @Test
  public void testAddPaymentDetail_Success() {
    String mockEmail = "email@email.com";
    int courseId = 1;

    AddPaymentRequest request = new AddPaymentRequest();
    request.setCourseId(courseId);

    Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
        .thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(courseRepository.findById(Mockito.eq(request.getCourseId())))
        .thenReturn(Optional.of(mockCourse));
    Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(mockPayment);
    Mockito.when(
            savedCourseRepository.findByCourseAndStudent(
                Mockito.any(Course.class), Mockito.any(Student.class)))
        .thenReturn(Optional.empty());
    Mockito.when(
            paymentDetailRepository.findByPaymentAndCourse(
                Mockito.any(Payment.class), Mockito.any(Course.class)))
        .thenReturn(Optional.empty());
    Mockito.when(paymentDetailRepository.save(Mockito.any(PaymentDetail.class)))
        .thenReturn(new PaymentDetail());

    AddPaymentResponse result = paymentService.addPaymentDetail(mockEmail, request);

    Assertions.assertNotNull(result);
    Assertions.assertEquals("Thêm khóa học vào hóa đơn thành công", result.getMessage());
  }

  @Test
  public void testAddPaymentDetail_UserNotFound() {
    String mockEmail = "email@email.com";
    int courseId = 1;

    AddPaymentRequest request = new AddPaymentRequest();
    request.setCourseId(courseId);

    Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail))).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> paymentService.addPaymentDetail(mockEmail, request));

    Assertions.assertEquals("Người dùng không tồn tại trong hệ thống", exception.getMessage());
  }

  @Test
  public void testAddPaymentDetail_AlreadySavedCourse() {
    String mockEmail = "email@email.com";
    int courseId = 1;

    AddPaymentRequest request = new AddPaymentRequest();
    request.setCourseId(courseId);

    Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
        .thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(courseRepository.findById(Mockito.eq(request.getCourseId())))
        .thenReturn(Optional.of(mockCourse));
    Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(mockPayment);
    Mockito.when(
            savedCourseRepository.findByCourseAndStudent(
                Mockito.any(Course.class), Mockito.any(Student.class)))
        .thenReturn(Optional.of(new SavedCourse()));

    Exception exception =
        Assertions.assertThrows(
            DataIntegrityViolationException.class,
            () -> paymentService.addPaymentDetail(mockEmail, request));

    Assertions.assertEquals("Khóa học đã được mua", exception.getMessage());
  }

  @Test
  public void testAddPaymentDetail_AlreadyHavePaymentDetail() {
    String mockEmail = "email@email.com";
    int courseId = 1;

    AddPaymentRequest request = new AddPaymentRequest();
    request.setCourseId(courseId);

    Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
        .thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(courseRepository.findById(Mockito.eq(request.getCourseId())))
        .thenReturn(Optional.of(mockCourse));
    Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(mockPayment);
    Mockito.when(
            savedCourseRepository.findByCourseAndStudent(
                Mockito.any(Course.class), Mockito.any(Student.class)))
        .thenReturn(Optional.empty());
    Mockito.when(
            paymentDetailRepository.findByPaymentAndCourse(
                Mockito.any(Payment.class), Mockito.any(Course.class)))
        .thenReturn(Optional.of(new PaymentDetail()));

    Exception exception =
        Assertions.assertThrows(
            DataIntegrityViolationException.class,
            () -> paymentService.addPaymentDetail(mockEmail, request));

    Assertions.assertEquals("Đã có khóa học trong hóa đơn", exception.getMessage());
  }

  @Test
  public void testCheckPayment_Success() {
    String mockEmail = "email@email.com";

    PaymentRequest request = new PaymentRequest();
    request.setPaymentId(1);
    mockPayment.setStatus(1);

    Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
        .thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(paymentRepository.findById(Mockito.eq(request.getPaymentId())))
        .thenReturn(Optional.of(mockPayment));

    Payment result = paymentService.checkPayment(mockEmail, request);

    Assertions.assertNotNull(result);
  }

  @Test
  public void testCheckPayment_UserNotFound() {
    String mockEmail = "email@email.com";

    PaymentRequest request = new PaymentRequest();
    request.setPaymentId(1);

    Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail))).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> paymentService.checkPayment(mockEmail, request));

    Assertions.assertEquals("Người dùng không tồn tại trong hệ thống", exception.getMessage());
  }

  @Test
  public void testCheckPayment_DoNotHaveAccess() {
    String mockEmail = "email@email.com";

    PaymentRequest request = new PaymentRequest();
    request.setPaymentId(1);

    Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
        .thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser))).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            AccessDeniedException.class, () -> paymentService.checkPayment(mockEmail, request));

    Assertions.assertEquals("Bạn không có quyền thanh toán khóa học", exception.getMessage());
  }

  @Test
  public void testCheckPayment_PaymentNotFound() {
    String mockEmail = "email@email.com";

    PaymentRequest request = new PaymentRequest();
    request.setPaymentId(1);

    Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
        .thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(paymentRepository.findById(Mockito.eq(request.getPaymentId())))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> paymentService.checkPayment(mockEmail, request));

    Assertions.assertEquals("Không tìm thấy hóa đơn", exception.getMessage());
  }

  @Test
  public void testCheckPayment_PaymentAlreadyPaid() {
    String mockEmail = "email@email.com";

    PaymentRequest request = new PaymentRequest();
    request.setPaymentId(1);

    Mockito.when(userRepository.findByEmail(Mockito.eq(mockEmail)))
        .thenReturn(Optional.of(mockUser));
    Mockito.when(studentRepository.findByUser(Mockito.eq(mockUser)))
        .thenReturn(Optional.of(mockStudent));
    Mockito.when(paymentRepository.findById(Mockito.eq(request.getPaymentId())))
        .thenReturn(Optional.of(mockPayment));

    Exception exception =
        Assertions.assertThrows(
            DataIntegrityViolationException.class,
            () -> paymentService.checkPayment(mockEmail, request));

    Assertions.assertEquals("Hóa đơn đã được thanh toán", exception.getMessage());
  }

  @Test
  public void testCallbackPayment_Success() {
    String vnp_TxnRef = "1";
    int paymentId = 1;

    Mockito.when(paymentRepository.findById(Mockito.eq(paymentId)))
        .thenReturn(Optional.of(mockPayment));
    Mockito.when(paymentDetailRepository.findByPayment(Mockito.any(Payment.class)))
        .thenReturn(List.of(new PaymentDetail()));
    Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(mockPayment);
    Mockito.when(savedCourseRepository.save(Mockito.any(SavedCourse.class)))
        .thenReturn(new SavedCourse());

    PaymentResponse result = paymentService.callbackPayment(vnp_TxnRef);

    Assertions.assertNotNull(result);
    Assertions.assertEquals("Thanh toán thành công", result.getMessage());
  }
}
