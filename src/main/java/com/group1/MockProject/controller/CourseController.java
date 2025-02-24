package com.group1.MockProject.controller;

import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.MessageDTO;
import com.group1.MockProject.dto.request.CourseRequest;
import com.group1.MockProject.dto.request.LectureRequest;
import com.group1.MockProject.dto.request.ReviewRequest;
import com.group1.MockProject.dto.request.SectionRequest;
import com.group1.MockProject.dto.response.*;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.exception.PaymentRequiredException;
import com.group1.MockProject.repository.*;
import com.group1.MockProject.service.*;
import com.group1.MockProject.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

  @Autowired private CourseService courseService;
  private final EnrollCourseService enrollCourseService;
  private final ReviewCourseService reviewCourseService;
  private final UserRepository userRepository;
  private final StudentRepository studentRepository;
  private final CourseRepository courseRepository;
  private final SavedCourseRepository savedCourseRepository;
  private final InstructorRepository instructorRepository;
  private final SectionService sectionService;
  private final LectureService lectureService;
  private final PaymentRepository paymentRepository;
  private final PaymentDetailRepository paymentDetailRepository;


  @PostMapping("/create")
  public ResponseEntity<?> createCourse(
      @RequestBody com.group1.MockProject.dto.request.CourseRequest courseRequest,
      HttpServletRequest request) {
    // Lấy token từ header Authorization
    String token = request.getHeader("Authorization");

    // Kiểm tra xem token có hợp lệ không
    if (token == null || !token.startsWith("Bearer ")) {
      throw new BadCredentialsException("Token xác thực không tìm thấy hoặc không đúng");
    }

    token = token.substring(7); // Loại bỏ "Bearer " để lấy chỉ phần token

    // Xác thực token
    if (!JwtUtil.validateToken(token)) {
      throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
    }

    // Lấy thông tin người dùng từ JWT
    //            String role = JwtUtil.extractRoleFromTokenCourse(token);
    //            int userId = JwtUtil.extractUserIdFromToken(token);

    String email = JwtUtil.extractEmail(token);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy nguời dùng", 1));

      // Kiểm tra nếu role là null hoặc không phải INSTRUCTOR
    //            if (user.getRole() == null) {
    //                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                        .body("Role not found in the token");
    //            }

    if (!user.getRole().toString().equals("INSTRUCTOR")) {
      throw new AccessDeniedException("Bạn không có quyền để tạo khoá học");
    }
    if (user.getStatus()==0){
      throw new IllegalArgumentException("Bạn không có quyền để tạo khoá học");
    }

    // Tiến hành tạo khóa học
    CourseDTO response = courseService.createCourse(courseRequest, token);


    // Trả về phản hồi thành công với mã trạng thái 201 Created và thông tin khóa học mới tạo
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponseDto.<CourseDTO>builder()
                .status(201)
                .message(HttpStatus.CREATED.getReasonPhrase())
                .response(response)
                .build()); // Trả về CourseDTO trong body của response
  }

  // Endpoint to update an existing course
  @PutMapping("/{courseId}")
  public ResponseEntity<?> updateCourse(
      @PathVariable int courseId,
      @RequestBody CourseRequest courseRequest,
      HttpServletRequest request) {
    // Lấy token từ header Authorization
    String token = request.getHeader("Authorization");

    // Kiểm tra xem token có hợp lệ không
    if (token == null || !token.startsWith("Bearer ")) {
      throw new BadCredentialsException("Token xác thực không tìm thấy hoặc không đúng");
    }

    token = token.substring(7); // Loại bỏ "Bearer " để lấy chỉ phần token

    // Xác thực token
    if (!JwtUtil.validateToken(token)) {
      throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
    }

    // Lấy thông tin người dùng từ JWT
    //            String role = JwtUtil.extractRoleFromTokenCourse(token);
    //            int instructorId = JwtUtil.extractUserIdFromToken(token);

    String email = JwtUtil.extractEmail(token);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    // Kiểm tra role
    if (user.getRole() == null) {
      throw new BadCredentialsException("Không tìm thấy vai trò trong token");
    }

    // Kiểm tra instructor
    if (user.getId() == 0) {
      throw new BadCredentialsException("Không tìm thấy người hướng dẫn trong token");
    }

    if (!user.getRole().toString().equals("INSTRUCTOR")) {
      throw new AccessDeniedException("Bạn không có quyền để cập nhật khoá học này");
    }
    //check status Instructor
    if (user.getStatus()==0){
      throw new IllegalArgumentException("Bạn không có quyền để update khoá học");
    }
    // Tiến hành tạo khóa học
    CourseDTO response = courseService.updateCourse(courseId, courseRequest, token);

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<CourseDTO>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  @DeleteMapping("/{courseId}")
  public ResponseEntity<?> deleteCourse(@PathVariable int courseId, HttpServletRequest request) {
    // Lấy token từ header Authorization
    String token = request.getHeader("Authorization");

    // Kiểm tra token
    if (token == null || !token.startsWith("Bearer ")) {
      throw new BadCredentialsException("Token xác thực không tìm thấy hoặc không đúng");
    }

    // Loại bỏ "Bearer " để lấy chỉ phần token
    token = token.substring(7);

    // Xác thực token
    if (!JwtUtil.validateToken(token)) {
      throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
    }

    String email = JwtUtil.extractEmail(token);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    // Kiểm tra role
    if (user.getRole() == null) {
      throw new BadCredentialsException("Không tìm thấy vai trò trong token");
    }

    // Kiểm tra role
    if (user.getId() == 0) {
      throw new BadCredentialsException("Không tìm thấy người hướng dẫn trong token");
    }

    if (!user.getRole().toString().equals("INSTRUCTOR")) {
      throw new AccessDeniedException("Bạn không có quyền để xoá khoá học này");
    }
    //check status Instructor
    if (user.getStatus()==0){
      throw new IllegalArgumentException("Bạn không có quyền để delete khoá học");
    }
    courseService.deleteCourse(courseId, token);

    // Trả về thông báo xóa thành công
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(
            ApiResponseDto.<CourseDTO>builder()
                .status(204)
                .message(HttpStatus.NO_CONTENT.getReasonPhrase())
                .build());
  }

  @GetMapping("/instructor/get-all-courses")
  public ResponseEntity<?> getCoursesByInstructor(HttpServletRequest request) {
    // Lấy token từ header Authorization
    String token = request.getHeader("Authorization");

    // Kiểm tra token
    if (token == null || !token.startsWith("Bearer ")) {
      throw new BadCredentialsException("Token xác thực không tìm thấy hoặc không đúng");
    }

    // Loại bỏ "Bearer " để lấy chỉ phần token
    token = token.substring(7);

    // Xác thực token
    if (!JwtUtil.validateToken(token)) {
      throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
    }

    String email = JwtUtil.extractEmail(token);
    User user =
            userRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    // Kiểm tra role
    if (user.getRole() == null) {
      throw new BadCredentialsException("Không tìm thấy vai trò trong token");
    }

    // Kiểm tra role
    if (user.getId() == 0) {
      throw new BadCredentialsException("Không tìm thấy người hướng dẫn trong token");
    }

//    if (!user.getRole().equals("ADMIN")) {
//      throw new AccessDeniedException("Bạn không có quyền xem danh sách khoá học này"+user.getRole().toString());
//    }
    List<CourseDTO> courses = courseService.getCoursesByInstructor(token);

    // Trả về danh sách khóa học dưới dạng API response
    return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponseDto.<List<CourseDTO>>builder()
                    .status(200)
                    .message(HttpStatus.OK.getReasonPhrase())
                    .response(courses)
                    .build());
  }


  @GetMapping("/{courseId}/enrolled")
  public ResponseEntity<?> enrollCourse(
      @PathVariable("courseId") int courseId,
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");
    Optional<User> user = userRepository.findByEmail(JwtUtil.decodeToken(token).getSubject());
    Optional<Student> student = studentRepository.findByUser(user.get());
    if (!(student.isPresent() && student.get().getUser().getStatus() == 1)) {
      throw new AccessDeniedException(
          "Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email của bạn để kích hoạt tài khoản.");
    }
    Optional<Course> course = courseRepository.findById(courseId);
    if (course.isEmpty()) {
      throw new EmptyResultDataAccessException(
          "Khóa học không hợp lệ. Vui lòng liên hệ quản trị viên để biết thêm thông tin.", 1);
    }
//    Optional<SavedCourse> savedCourse =
//        savedCourseRepository.findByCourseAndStudent(course.get(), student.get());
//    if (savedCourse.isEmpty()) {
//      throw new PaymentRequiredException(
//          "Bạn không có quyền truy cập khóa học này. Vui lòng thanh toán khóa học trước khi truy cập.");
//    }
    Payment payment = paymentRepository.findByStudent(student.get());
    List<PaymentDetail> paymentDetails = paymentDetailRepository.findByPayment(payment);
    boolean check = false;
    for (PaymentDetail paymentDetail : paymentDetails) {
      if (paymentDetail.getCourse().equals(course.get())) {
        check = true;
      }
    }
    if (!check){
      throw new PaymentRequiredException("Bạn không có quyền truy cập khóa học này. Vui lòng thanh toán khóa học trước khi truy cập.");
    }
    String response = enrollCourseService.addEnroll(student.get(), course.get());

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<MessageDTO>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(new MessageDTO(response))
                .build());
  }

  @GetMapping("/{courseId}/finished")
  public ResponseEntity<?> finishedCourse(
      @PathVariable("courseId") int courseId,
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");
    Optional<User> user = userRepository.findByEmail(JwtUtil.decodeToken(token).getSubject());
    Optional<Student> student = studentRepository.findByUser(user.get());
    if (student.isEmpty()) {
      throw new AccessDeniedException(
          "Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email của bạn để kích hoạt tài khoản.");
    }
    Optional<Course> course = courseRepository.findById(courseId);
    if (course.isEmpty()) {
      throw new EmptyResultDataAccessException(
          "Khóa học không hợp lệ. Vui lòng liên hệ quản trị viên để biết thêm thông tin.", 1);
    }
    Optional<SavedCourse> savedCourse =
        savedCourseRepository.findByCourseAndStudent(course.get(), student.get());
    if (!(savedCourse.isPresent() && savedCourse.get().getStatus() == 0)) {
      throw new DataIntegrityViolationException("Bạn đã hoàn thành khóa học này rồi.");
    }
    savedCourse.get().setStatus(1);
    savedCourseRepository.save(savedCourse.get());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<MessageDTO>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(new MessageDTO("Xin chúc mừng bạn đã hoàn thành khóa học."))
                .build());
  }

  @PostMapping("/{courseId}/review")
  public ResponseEntity<?> reviewCourse(
      @RequestBody ReviewRequest request,
      @PathVariable("courseId") int courseId,
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");
    Optional<User> user = userRepository.findByEmail(JwtUtil.decodeToken(token).getSubject());
    Optional<Student> student = studentRepository.findByUser(user.get());
    if (student.isEmpty()) {
      throw new AccessDeniedException(
          "Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email của bạn để kích hoạt tài khoản.");
    }
    Optional<Course> course = courseRepository.findById(courseId);
    if (course.isEmpty()) {
      throw new EmptyResultDataAccessException(
          "Khóa học không hợp lệ. Vui lòng liên hệ quản trị viên để biết thêm thông tin.", 1);
    }
    Optional<SavedCourse> savedCourse =
        savedCourseRepository.findByCourseAndStudent(course.get(), student.get());
    if (!(savedCourse.isPresent() && savedCourse.get().getStatus() == 1)) {
      throw new AccessDeniedException(
          "Bạn chưa hoàn thành khóa học, không thể đánh giá khóa học này.");
    }
    String response = reviewCourseService.addReview(request, student.get(), course.get());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<MessageDTO>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(new MessageDTO(response))
                .build());
  }

  @GetMapping("/{instructorId}/reviews")
  public ResponseEntity<ApiResponseDto<List<ReviewResponse>>> getAllReviewsOfInstructor(
      @PathVariable("instructorId") int instructorId,
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");
    ///////// tim bang user
    Optional<User> user = userRepository.findByEmail(JwtUtil.decodeToken(token).getSubject());
    Optional<Instructor> instructor = instructorRepository.findById(instructorId);
    if (instructor.isEmpty()) {
      throw new EmptyResultDataAccessException(
          "Không tìm thấy instructor hoặc bạn không có quyền để xem đánh giá. Vui lòng liên hệ quản trị viên để biết thêm thông tin.",
          1);
    }

    List<ReviewResponse> response = reviewCourseService.getAllReviews(instructor.get());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<List<ReviewResponse>>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  // xem list các khóa học bị từ chối
  @GetMapping("/rejects")
  public ResponseEntity<ApiResponseDto<List<RejectCourseResponse>>> getAllRejects(
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");

    // Xác thực token
    if (!JwtUtil.validateToken(token)) {
      throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
    }

    User user =
        userRepository
            .findByEmail(JwtUtil.decodeToken(token).getSubject())
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy nguời dùng", 1));

    if (!user.getRole().toString().equals("INSTRUCTOR")) {
      throw new AccessDeniedException("Bạn không có quyền để thực hiện chức năng này!");
    }

    Optional<Instructor> instructor =
        instructorRepository.findInstructorByUser(user.getInstructor().getUser());

    if (instructor.isEmpty()) {
      throw new AccessDeniedException(
          "Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email của bạn để kích hoạt tài khoản.");
    }

    List<RejectCourseResponse> response =
        courseService.viewAllRejectedCoursesByInstructor(instructor.get().getId());

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<List<RejectCourseResponse>>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  // re-submit course
  @PutMapping("/{courseId}/re-submit")
  public ResponseEntity<?> reSubmitCourse(
      @PathVariable int courseId,
      @RequestBody CourseRequest courseRequest,
      HttpServletRequest request) {
    // Lấy token từ header Authorization
    String token = request.getHeader("Authorization");

    // Kiểm tra xem token có hợp lệ không
    if (token == null || !token.startsWith("Bearer ")) {
      throw new BadCredentialsException("Token xác thực không tìm thấy hoặc không đúng");
    }

    token = token.substring(7); // Loại bỏ "Bearer " để lấy chỉ phần token

    // Xác thực token
    if (!JwtUtil.validateToken(token)) {
      throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
    }

    String email = JwtUtil.extractEmail(token);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    if (!user.getRole().toString().equals("INSTRUCTOR")) {
      throw new AccessDeniedException("Bạn không có quyền để thực hiện chức năng này!");
    }

    Optional<Instructor> instructor =
        instructorRepository.findInstructorByUser(user.getInstructor().getUser());

    if (instructor.isEmpty()) {
      throw new AccessDeniedException(
          "Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email của bạn để kích hoạt tài khoản.");
    }

    CourseDTO response = courseService.reSubmitCourse(courseRequest, token, courseId);

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<CourseDTO>builder()
                .status(200)
                .message("Cập nhật thành công. Khóa học của bạn đang được quản trị viên xử lý.")
                .response(response)
                .build());
  }
  @GetMapping("/student/get-all-courses")
  public ResponseEntity<?> getAllCourses(HttpServletRequest request) {
    // Lấy token từ header Authorization
    String token = request.getHeader("Authorization");

    // Kiểm tra token
    if (token == null || !token.startsWith("Bearer ")) {
      throw new BadCredentialsException("Token xác thực không tìm thấy hoặc không đúng");
    }

    // Loại bỏ "Bearer " để lấy chỉ phần token
    token = token.substring(7);

    // Xác thực token
    if (!JwtUtil.validateToken(token)) {
      throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
    }

    List<CourseDTO> courses = courseService.studentGetAllCourses(token);

    // Trả về danh sách khóa học dưới dạng API response
    return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponseDto.<List<CourseDTO>>builder()
                    .status(200)
                    .message(HttpStatus.OK.getReasonPhrase())
                    .response(courses)
                    .build());
  }

  /*
   * CRUD SECTION
   *  */
  // GET /api/v1/courses/{courseId}/sections - Get all sections
  @PreAuthorize("hasRole('INSTRUCTOR')")
  @GetMapping("/{courseId}/sections")
  public ResponseEntity<?> getAllSections(
          @PathVariable("courseId") String requestCourseId,
          Authentication authentication
  ) {
    try {
      int courseId = Integer.parseInt(requestCourseId);
      String email = authentication.getName();
      List<SectionResponse> response = sectionService.getAllSection(courseId, email);

      return ResponseEntity.ok(
              ApiResponseDto.<List<SectionResponse>>builder()
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
  @PreAuthorize("hasRole('INSTRUCTOR')")
  @GetMapping("/{courseId}/sections/{sectionId}")
  public ResponseEntity<?> getSectionById(
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
      SectionResponse response = sectionService.getSectionById(courseId, sectionId,email);

      return ResponseEntity.ok(
              ApiResponseDto.<SectionResponse>builder()
                      .status(200)
                      .message(HttpStatus.OK.getReasonPhrase())
                      .response(response)
                      .build()
      );
    } catch (NumberFormatException e) {

      return ResponseEntity.badRequest().body("Mã khóa học không hợp lệ: " + requestCourseId);
    }
  }

  // POST /api/v1/courses/{courseId}/sections - Add new section
  @PreAuthorize("hasRole('INSTRUCTOR')")
  @PostMapping("/{courseId}/sections")
  public ResponseEntity<?> createNewSection(
          @PathVariable("courseId") String requestCourseId,
          @RequestBody @Valid SectionRequest request,
          Authentication authentication
  ) {
    if (requestCourseId == null || requestCourseId.isEmpty()) {
      return ResponseEntity.badRequest().body("URL không hợp lệ");
    }
    try {
      int courseId = Integer.parseInt(requestCourseId);
      String email = authentication.getName();
      SectionResponse response = sectionService.createSection(courseId, request, email);

      return ResponseEntity.ok(
              ApiResponseDto.<SectionResponse>builder()
                      .status(HttpStatus.CREATED.value())
                      .message(HttpStatus.CREATED.getReasonPhrase())
                      .response(response)
                      .build()
      );
    } catch (NumberFormatException e) {

      return ResponseEntity.badRequest().body("Mã khóa học không hợp lệ: " + requestCourseId);
    }
  }

  // PUT /api/v1/courses/{courseId}/sections/{sectionId} - Update existed section
  @PreAuthorize("hasRole('INSTRUCTOR')")
  @PutMapping("/{courseId}/sections/{sectionId}")
  public ResponseEntity<?> updateExistingSection(
          @PathVariable("courseId") String requestCourseId,
          @PathVariable("sectionId") String requestSectionId,
          @RequestBody @Valid SectionRequest request,
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
      SectionResponse response = sectionService.updateSection(courseId, sectionId, request, email);

      return ResponseEntity.ok(
              ApiResponseDto.<SectionResponse>builder()
                      .status(HttpStatus.OK.value())
                      .message(HttpStatus.OK.getReasonPhrase())
                      .response(response)
                      .build()
      );
    } catch (NumberFormatException e) {

      return ResponseEntity.badRequest().body("Mã khóa học không hợp lệ: " + requestCourseId);
    }
  }

  // DELETE /api/v1/courses/{courseId}/section/{sectionId} - Delete section by id
  @PreAuthorize("hasRole('INSTRUCTOR')")
  @DeleteMapping("/{courseId}/sections/{sectionId}")
  public ResponseEntity<?> deleleSectionById(
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
      sectionService.deleteSectionById(courseId, sectionId, email);
      return ResponseEntity.status(HttpStatus.NO_CONTENT)
              .body(
                      ApiResponseDto.<String>builder()
                              .status(204)
                              .message(HttpStatus.NO_CONTENT.getReasonPhrase())
                              .build());
    } catch (NumberFormatException e) {

      return ResponseEntity.badRequest().body("Mã khóa học không hợp lệ: " + requestCourseId);
    }
  }

  /*
  * CRUD LECTURES
  *  */
  // GET /api/v1/courses/{courseId}/section/{sectionId}/lectures - Get all lectures
  @PreAuthorize("hasRole('INSTRUCTOR')")
  @GetMapping("/{courseId}/sections/{sectionId}/lectures")
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
      List<LectureResponse> response = lectureService.getAllLecturesBelongToSection(courseId, sectionId, email);

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

  // GET /api/v1/courses/{courseId}/sections/{sectionId}/lecture/{lectureId} - Get lecture by id
  @PreAuthorize("hasRole('INSTRUCTOR')")
  @GetMapping("/{courseId}/sections/{sectionId}/lectures/{lectureId}")
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
      LectureResponse response = lectureService.getLectureById(courseId, sectionId,lectureId, email);

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

  // POST /api/v1/courses/{courseId}/sections/{sectionId}/lectures - Add new lecture
  @PreAuthorize("hasRole('INSTRUCTOR')")
  @PostMapping("/{courseId}/sections/{sectionId}/lectures")
  public ResponseEntity<?> createNewLecture(
          @PathVariable("courseId") String requestCourseId,
          @PathVariable("sectionId") String requestSectionId,
          @RequestParam("name") @NotBlank(message = "Vui lòng điền tên bài giảng") String name,
          @RequestParam("description") @NotBlank(message = "Vui lòng điền mô tả của bài giảng") String description,
          @RequestParam("file") MultipartFile multipartFile,
          Authentication authentication
  ) {
    if (requestCourseId == null || requestCourseId.isEmpty() ||
            requestSectionId == null || requestSectionId.isEmpty()) {
      return ResponseEntity.badRequest().body("URL không hợp lệ");
    }
    if (multipartFile == null || multipartFile.isEmpty()) {
      return ResponseEntity.badRequest().body("File không được để trống");
    }

    try {
      int courseId = Integer.parseInt(requestCourseId);
      long sectionId = Long.parseLong(requestSectionId);
      String email = authentication.getName();
      LectureResponse response = lectureService.createLecture(courseId, sectionId, name, description, multipartFile, email);

      return ResponseEntity.ok(
              ApiResponseDto.<LectureResponse>builder()
                      .status(HttpStatus.CREATED.value())
                      .message(HttpStatus.CREATED.getReasonPhrase())
                      .response(response)
                      .build()
      );
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().body("Id không hợp lệ");
    } catch (IOException e) {
      return ResponseEntity.badRequest().body("Có lỗi xảy ra");
    }

  }

  // PUT /api/v1/courses/{courseId}/sections/{sectionId}/lectures/{lecturesId} - Update existed lecture
  @PreAuthorize("hasRole('INSTRUCTOR')")
  @PutMapping("/{courseId}/sections/{sectionId}/lectures/{lectureId}")
  public ResponseEntity<?> updateLectureById(
          @PathVariable("courseId") String requestCourseId,
          @PathVariable("sectionId") String requestSectionId,
          @PathVariable("lectureId") String requestLectureId,
          @RequestParam(value = "name", required = false) @NotBlank(message = "Vui lòng điền tên bài giảng") String name,
          @RequestParam(value = "description", required = false) @NotBlank(message = "Vui lòng điền mô tả của bài giảng") String description,
          @RequestParam(value = "file", required = false) MultipartFile multipartFile,
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
      LectureResponse response = lectureService.updateLectureById(courseId, sectionId, lectureId,name, description, multipartFile, email);

      return ResponseEntity.ok(
              ApiResponseDto.<LectureResponse>builder()
                      .status(HttpStatus.OK.value())
                      .message(HttpStatus.OK.getReasonPhrase())
                      .response(response)
                      .build()
      );
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().body("Id không hợp lệ");
    }  catch (IOException e) {
      return ResponseEntity.badRequest().body("Có lỗi xảy ra");
    }
  }

  // DELETE /api/v1/courses/{courseId}/sections/{sectionId}/lectures/{lecturesId} - Delete lecture by id
  // PUT /api/v1/courses/{courseId}/sections/{sectionId}/lectures/{lecturesId} - Update existed lecture
  @PreAuthorize("hasRole('INSTRUCTOR')")
  @DeleteMapping("/{courseId}/sections/{sectionId}/lectures/{lectureId}")
  public ResponseEntity<?> deleteLectureById(
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
      if (lectureService.deleteLectureById(courseId, sectionId, lectureId, email)) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(
                        ApiResponseDto.<CourseDTO>builder()
                                .status(204)
                                .message(HttpStatus.NO_CONTENT.getReasonPhrase())
                                .build());
      };

      return ResponseEntity.ok("Xóa bài giảng thất bại");
    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest().body("Id không hợp lệ");
    }
  }
}
