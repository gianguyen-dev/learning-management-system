package com.group1.MockProject.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group1.MockProject.dto.PaymentDTO;
import com.group1.MockProject.dto.request.AddPaymentRequest;
import com.group1.MockProject.dto.request.PaymentRequest;
import com.group1.MockProject.dto.response.*;
import com.group1.MockProject.entity.Category;
import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Payment;
import com.group1.MockProject.exception.GlobalExceptionHandler;
import com.group1.MockProject.repository.CategoryRepository;
import com.group1.MockProject.repository.CourseRepository;
import com.group1.MockProject.service.PaymentService;
import com.group1.MockProject.service.SavedCourseService;
import com.group1.MockProject.service.StudentService;
import com.group1.MockProject.service.implementation.VNPayServiceImpl;
import com.group1.MockProject.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

  private MockMvc mockMvc;

  @Mock private StudentService studentService;

  @Mock private JwtUtil jwtUtil;

  @Mock private CourseRepository courseRepository;

  @Mock private CategoryRepository categoryRepository;

  @Mock private ModelMapper modelMapper;

  @Mock private SavedCourseService savedCourseService;

  @Mock private PaymentService paymentService;

  @Mock private VNPayServiceImpl VNPayService;

  @InjectMocks private StudentController studentController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  String mockToken = "mockToken";
  String mockEmail = "mock@email.com";

  private MockedStatic<JwtUtil> jwtUtilMock;

  @BeforeEach
  void setUp() {

    jwtUtilMock = Mockito.mockStatic(JwtUtil.class);
    jwtUtilMock.when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken))).thenReturn(mockEmail);
    jwtUtilMock.when(() -> JwtUtil.validateToken(Mockito.eq(mockToken))).thenReturn(true);

    studentController =
        new StudentController(
            studentService,
            jwtUtil,
            courseRepository,
            categoryRepository,
            modelMapper,
            savedCourseService,
            paymentService,
            VNPayService);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(studentController)
            .alwaysDo(print())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @AfterEach
  void tearDown() {
    if (jwtUtilMock != null) {
      jwtUtilMock.close();
    }
  }

  @Test
  public void testViewListSubscription_Success() throws Exception {

    InstructorResponse instructorResponse = new InstructorResponse();
    instructorResponse.setId(1);
    instructorResponse.setName("Test");
    instructorResponse.setExpertise("Test");

    Mockito.when(studentService.viewListSubscription(Mockito.eq(mockEmail)))
        .thenReturn(List.of(instructorResponse));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/student/view-list-subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + mockToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testAddPaymentDetail_Success() throws Exception {

    String requestBody =
        """
{
  "courseId": "12345",
  "paymentDate": "2024-12-29T10:15:30"
}
""";

    AddPaymentResponse addPaymentResponse = new AddPaymentResponse();
    addPaymentResponse.setMessage("Test");

    Mockito.when(
            paymentService.addPaymentDetail(
                Mockito.eq(mockEmail), Mockito.any(AddPaymentRequest.class)))
        .thenReturn(addPaymentResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/student/add-payment")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + mockToken)
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testSearchInstructor_Success() throws Exception {
    String instructorName = "Instructor";

    InstructorResponse instructorResponse = new InstructorResponse();
    instructorResponse.setId(1);
    instructorResponse.setName(instructorName);

    List<InstructorResponse> instructors = List.of(instructorResponse);

    Mockito.when(studentService.searchInstructor(Mockito.eq(instructorName)))
        .thenReturn(instructors);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/student/search-instructor")
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", instructorName)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testSubscribeToInstructor_Success() throws Exception {
    int instructorId = 1;
    String responseMessage = "Đăng ký theo dõi giảng viên thành công";

    Mockito.when(
            studentService.subscribeToInstructor(Mockito.eq(mockEmail), Mockito.eq(instructorId)))
        .thenReturn(responseMessage);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/student/subscribe/" + instructorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + mockToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists())
        .andExpect(jsonPath("$.response.message").value(responseMessage));
  }

  @Test
  public void testSearchCourseByCategory() throws Exception {
    int categoryId = 1;
    Category mockCategory = new Category();
    mockCategory.setId(categoryId);

    Course mockCourse = new Course();
    mockCourse.setId(1);
    mockCourse.setPrice(50000.0);
    mockCourse.setCategory(mockCategory);

    CourseDTO mockCourseDTO = new CourseDTO();

    Mockito.when(categoryRepository.findById(Mockito.eq(categoryId)))
        .thenReturn(Optional.of(mockCategory));
    Mockito.when(courseRepository.findCourseByCategory(Mockito.eq(mockCategory)))
        .thenReturn(List.of(mockCourse));
    Mockito.when(modelMapper.map(mockCourse, CourseDTO.class)).thenReturn(mockCourseDTO);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/student/category/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testUnsubscribeToInstructor_Success() throws Exception {
    String mockToken = "mockToken";
    String mockEmail = "mock@email.com";
    int instructorId = 1;
    String responseMessage = "Huỷ theo dõi giảng viên thành công";

    Mockito.when(
            studentService.unsubscribeFromInstructor(
                Mockito.eq(mockEmail), Mockito.eq(instructorId)))
        .thenReturn(responseMessage);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/student/unsubscribe/" + instructorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + mockToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists())
        .andExpect(jsonPath("$.response.message").value(responseMessage));
  }

  @Test
  public void testPayment_Success() throws Exception {
    Payment payment = new Payment();
    payment.setId(1);
    PaymentRequest paymentRequest = new PaymentRequest();
    paymentRequest.setPaymentId(payment.getId());

    PaymentDTO.VNPayResponse response =
        PaymentDTO.VNPayResponse.builder().paymentUrl("mockUrl").build();

    Mockito.when(
            paymentService.checkPayment(Mockito.eq(mockEmail), Mockito.any(PaymentRequest.class)))
        .thenReturn(payment);
    Mockito.when(
            VNPayService.createVnPayPayment(
                Mockito.any(Payment.class), Mockito.any(HttpServletRequest.class)))
        .thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/student/payment/vn-pay")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + mockToken)
                .content(objectMapper.writeValueAsString(paymentRequest))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testPayCallbackHandler_Success() throws Exception {
    String status = "00";
    PaymentResponse paymentResponse = new PaymentResponse();
    paymentResponse.setMessage("Thanh toán thành công");
    Mockito.when(paymentService.callbackPayment(Mockito.anyString())).thenReturn(paymentResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/student/payment/vn-pay-callback")
                .contentType(MediaType.APPLICATION_JSON)
                .param("vnp_ResponseCode", status)
                .param("vnp_TxnRef", "1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists())
        .andExpect(jsonPath("$.response.message").value("Thanh toán thành công"));
  }

  @Test
  public void testPayCallbackHandler_InvalidStatus() throws Exception {
    String status = "01";

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/student/payment/vn-pay-callback")
                .contentType(MediaType.APPLICATION_JSON)
                .param("vnp_ResponseCode", status)
                .param("vnp_TxnRef", "1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists())
        .andExpect(jsonPath("$.response.message").value("Thanh toán thất bại"));
  }

  @Test
  public void testViewSavedCourse_Success() throws Exception {
    GetSavedCourseResponse getSavedCourseResponse = new GetSavedCourseResponse();

    Mockito.when(savedCourseService.getSavedCoursesByEmail(Mockito.eq(mockEmail)))
        .thenReturn(getSavedCourseResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/student/savedCourses")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + mockToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }
}
