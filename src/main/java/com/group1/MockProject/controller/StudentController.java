package com.group1.MockProject.controller;

import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.MessageDTO;
import com.group1.MockProject.dto.PaymentDTO;
import com.group1.MockProject.dto.request.AddPaymentRequest;
import com.group1.MockProject.dto.request.PaymentRequest;
import com.group1.MockProject.dto.response.*;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Payment;
import com.group1.MockProject.exception.UnprocessableEntityException;
import com.group1.MockProject.repository.CategoryRepository;
import com.group1.MockProject.repository.CourseRepository;
import com.group1.MockProject.repository.PaymentRepository;
import com.group1.MockProject.service.*;
import com.group1.MockProject.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {
  private final StudentService studentService;
  private final JwtUtil jwtUtil;
  private final CourseRepository courseRepository;
  private final CategoryRepository categoryRepository;
  private final ModelMapper modelMapper;
  private final SavedCourseService savedCourseService;
  private final PaymentService paymentService;
  private final VNPayService VNPayService;
  private final PaymentRepository paymentRepository;
    private final SectionService sectionService;
    private final LectureService lectureService;

  @GetMapping("/view-list-subscription")
  public ResponseEntity<ApiResponseDto<List<InstructorResponse>>> viewListSubscription(
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");
    String email = jwtUtil.extractEmail(token);
    List<InstructorResponse> instructors = studentService.viewListSubscription(email);
    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<List<InstructorResponse>>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(instructors)
                .build());
  }

  @GetMapping("/add-payment")
  public ResponseEntity<ApiResponseDto<AddPaymentResponse>> AddPaymentDetail(
      @RequestHeader("Authorization") String authorizationHeader,
      @Valid @RequestBody AddPaymentRequest request) {
    String token = authorizationHeader.replace("Bearer ", "");
    String email = JwtUtil.extractEmail(token);
    AddPaymentResponse response = paymentService.addPaymentDetail(email, request);
    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<AddPaymentResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  @GetMapping("/search-instructor")
  public ResponseEntity<ApiResponseDto<List<InstructorResponse>>> searchInstructor(
      @RequestParam String name) {
    List<InstructorResponse> instructors = studentService.searchInstructor(name);
    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<List<InstructorResponse>>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(instructors)
                .build());
  }

  @PostMapping("/subscribe/{instructorId}")
  public ResponseEntity<ApiResponseDto<?>> subscribeToInstructor(
      @PathVariable Integer instructorId,
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");
    String email = JwtUtil.extractEmail(token);
    String response = studentService.subscribeToInstructor(email, instructorId);

    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<MessageDTO>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(new MessageDTO(response))
                .build());
  }

  @PostMapping("/unsubscribe/{instructorId}")
  public ResponseEntity<ApiResponseDto<?>> unsubscribeFromInstructor(
      @PathVariable Integer instructorId,
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");
    String email = jwtUtil.extractEmail(token);
    String response = studentService.unsubscribeFromInstructor(email, instructorId);

    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<MessageDTO>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(new MessageDTO(response))
                .build());
  }

  @GetMapping("/payment/vn-pay")
  public ResponseEntity<ApiResponseDto<?>> payment(
      @RequestHeader("Authorization") String authorizationHeader,
      @Valid @RequestBody PaymentRequest request,
      HttpServletRequest httpRequest) {
    String token = authorizationHeader.replace("Bearer ", "");
    String email = JwtUtil.extractEmail(token);
    Payment payment = paymentService.checkPayment(email, request);
    if (payment.getTotal_price() == 0.0) {

      PaymentResponse response = paymentService.freePayment(payment);

      return ResponseEntity.ok()
          .body(
              ApiResponseDto.<PaymentResponse>builder()
                  .status(200)
                  .message(HttpStatus.OK.getReasonPhrase())
                  .response(response)
                  .build());
    }

    if (payment.getTotal_price() <= 5000.0 && payment.getTotal_price() != 0.0) {
      paymentRepository.delete(payment);
      throw new UnprocessableEntityException("Tổng hoá đơn phải có giá trị tối thiểu 5000 VNĐ");
    }

    PaymentDTO.VNPayResponse response = VNPayService.createVnPayPayment(payment, httpRequest);
    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<PaymentDTO.VNPayResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  @GetMapping("/payment/vn-pay-callback")
  public ResponseEntity<ApiResponseDto<PaymentResponse>> payCallbackHandler(
      HttpServletRequest request) {
    String status = request.getParameter("vnp_ResponseCode");
    if (status.equals("00")) {
      PaymentResponse response = paymentService.callbackPayment(request.getParameter("vnp_TxnRef"));
      return ResponseEntity.ok()
          .body(
              ApiResponseDto.<PaymentResponse>builder()
                  .status(200)
                  .message(HttpStatus.OK.getReasonPhrase())
                  .response(response)
                  .build());
    } else {
      PaymentResponse response =
          paymentService.callbackPaymentFail(request.getParameter("vnp_TxnRef"));
      return ResponseEntity.ok()
          .body(
              ApiResponseDto.<PaymentResponse>builder()
                  .status(200)
                  .message(HttpStatus.OK.getReasonPhrase())
                  .response(response)
                  .build());
    }
  }

  //  @GetMapping("/payment/vn-pay")
  //  public ResponseEntity<ApiResponseDto<PaymentDTO.VNPayResponse>> payment(
  //      @RequestHeader("Authorization") String authorizationHeader,
  //      @Valid @RequestBody PaymentRequest request,
  //      HttpServletRequest httpRequest) {
  //    String token = authorizationHeader.replace("Bearer ", "");
  //    String email = JwtUtil.decodeToken(token).getSubject();
  //    Payment payment = paymentService.checkPayment(email, request);
  //    PaymentDTO.VNPayResponse response = VNPayService.createVnPayPayment(payment, httpRequest);
  //    return ResponseEntity.ok()
  //        .body(
  //            ApiResponseDto.<PaymentDTO.VNPayResponse>builder()
  //                .status(200)
  //                .message(HttpStatus.OK.getReasonPhrase())
  //                .response(response)
  //                .build());
  //  }

  @GetMapping("/savedCourses")
  public ResponseEntity<ApiResponseDto<GetSavedCourseResponse>> viewSavedCourse(
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");
    String email = JwtUtil.extractEmail(token);
    GetSavedCourseResponse response = savedCourseService.getSavedCoursesByEmail(email);
    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<GetSavedCourseResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  //  @GetMapping("/create-savedCourses/{id}")
  //  public ResponseEntity<?> createSavedCourses(@RequestHeader("Authorization") String
  // authorizationHeader, @PathVariable Integer id) {
  //    String token = authorizationHeader.replace("Bearer ", "");
  //    String email = JwtUtil.decodeToken(token).getSubject();
  ////    SavedCourse savedCourseList = savedCourseService.createSavedCourses(email, id);
  //
  //    return ResponseEntity.ok().body(
  //            ApiResponseDto.<SavedCourse>builder()
  //                    .status(200)
  //                    .message(HttpStatus.OK.getReasonPhrase())
  //                    .response(savedCourseList)
  //                    .build()
  //    );
  //  }

  @GetMapping("/category/{id}")
  public ResponseEntity<ApiResponseDto<List<CourseDTO>>> searchCourseByCategory(
      @PathVariable int id) {
    List<Course> courses =
        courseRepository.findCourseByCategory(categoryRepository.findCategoryById(id));
    List<CourseDTO> courseDTOList =
        courses.stream()
            .map(course -> modelMapper.map(course, CourseDTO.class))
            .collect(Collectors.toList());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<List<CourseDTO>>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(courseDTOList)
                .build());
  }

  //  @GetMapping("/payment")
  //  public ResponseEntity<ApiResponseDto<PaymentResponse>> payment(
  //      @RequestHeader("Authorization") String authorizationHeader,
  //      @Valid @RequestBody PaymentRequest request) {
  //    String token = authorizationHeader.replace("Bearer ", "");
  //    String email = JwtUtil.decodeToken(token).getSubject();
  //    PaymentResponse response = paymentService.addPayment(email, request);
  //    return ResponseEntity.ok()
  //        .body(
  //            ApiResponseDto.<PaymentResponse>builder()
  //                .status(200)
  //                .message(HttpStatus.OK.getReasonPhrase())
  //                .response(response)
  //                .build());
  //  }

  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/courses/{courseId}/sections")
  public ResponseEntity<?> getAllSections(
          @PathVariable("courseId") String requestCourseId,
          Authentication authentication
  ) {
    try {
      int courseId = Integer.parseInt(requestCourseId);
      String email = authentication.getName();
      List<?> response = sectionService.getAllSectionForStudent(courseId, email);

      return ResponseEntity.ok(
              ApiResponseDto.<List<?>>builder()
                      .status(200)
                      .message(HttpStatus.OK.getReasonPhrase())
                      .response(response)
                      .build()
      );
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().body("Mã khóa học không hợp lệ: " + requestCourseId);
    }
  }


  // GET /api/v1/courses/{id}/section/{sectionId} - Get section by id
  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/courses/{courseId}/sections/{sectionId}")
  public ResponseEntity<?> getSectionByIdForStudent(
          @PathVariable("courseId") String requestCourseId,
          @PathVariable("sectionId") String requestSectionId,
          Authentication authentication
  ) {
    if (requestCourseId == null || requestCourseId.isEmpty() ||
            requestSectionId == null || requestSectionId.isEmpty()) {
      return ResponseEntity.badRequest().body("URL không hợp lệ");
    }
    try {
      int courseId = Integer.parseInt(requestCourseId);
      long sectionId = Long.parseLong(requestSectionId);
      String email = authentication.getName();
      Object response = sectionService.getSectionByIdForStudent(courseId, sectionId,email);

      return ResponseEntity.ok(
              ApiResponseDto.<Object>builder()
                      .status(200)
                      .message(HttpStatus.OK.getReasonPhrase())
                      .response(response)
                      .build()
      );
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().body("Mã khóa học không hợp lệ: " + requestCourseId);
    }
  }

  // GET /api/v1/courses/{courseId}/section/{sectionId}/lectures - Get all lectures
  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/courses/{courseId}/sections/{sectionId}/lectures")
  public ResponseEntity<?> getSectionLectures(
          @PathVariable("courseId") String requestCourseId,
          @PathVariable("sectionId") String requestSectionId,
          Authentication authentication
  ) {
    if (requestCourseId == null || requestCourseId.isEmpty() ||
            requestSectionId == null || requestSectionId.isEmpty()) {
      return ResponseEntity.badRequest().body("URL không hợp lệ");
    }
    try {
      int courseId = Integer.parseInt(requestCourseId);
      long sectionId = Long.parseLong(requestSectionId);
      String email = authentication.getName();
      List<LectureResponse> response = lectureService.getAllLecturesBelongToSectionForStudent(courseId, sectionId, email);

      return ResponseEntity.ok(
              ApiResponseDto.<List<LectureResponse>>builder()
                      .status(HttpStatus.OK.value())
                      .message(HttpStatus.OK.getReasonPhrase())
                      .response(response)
                      .build()
      );
    } catch (NumberFormatException e) {

      return ResponseEntity.badRequest().body("Mã khóa học không hợp lệ: " + requestCourseId);
    }
  }

  // GET /api/v1/student/courses/{courseId}/sections/{sectionId}/lecture/{lectureId} - Get lecture by id
  @PreAuthorize("hasRole('STUDENT')")
  @GetMapping("/courses/{courseId}/sections/{sectionId}/lectures/{lectureId}")
  public ResponseEntity<?> getLectureById(
          @PathVariable("courseId") String requestCourseId,
          @PathVariable("sectionId") String requestSectionId,
          @PathVariable("lectureId") String requestLectureId,
          Authentication authentication
  ) {
    if (requestCourseId == null || requestCourseId.isEmpty() ||
            requestSectionId == null || requestSectionId.isEmpty() ||
            requestLectureId == null || requestLectureId.isEmpty()) {
      return ResponseEntity.badRequest().body("URL không hợp lệ");
    }
    try {
      int courseId = Integer.parseInt(requestCourseId);
      long sectionId = Long.parseLong(requestSectionId);
      long lectureId = Long.parseLong(requestLectureId);
      String email = authentication.getName();
      LectureResponse response = lectureService.getLectureByIdForStudent(courseId, sectionId,lectureId, email);

      return ResponseEntity.ok(
              ApiResponseDto.<LectureResponse>builder()
                      .status(HttpStatus.OK.value())
                      .message(HttpStatus.OK.getReasonPhrase())
                      .response(response)
                      .build()
      );
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().body("Mã khóa học không hợp lệ: " + requestCourseId);
    }
  }
}
