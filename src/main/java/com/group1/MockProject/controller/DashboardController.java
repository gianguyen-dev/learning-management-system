package com.group1.MockProject.controller;

import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.response.InstructorDashboardResponse;
import com.group1.MockProject.dto.response.StudentDashboardResponse;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

  private final InstructorRepository instructorRepository;
  private final UserRepository userRepository;

  @Autowired
  public DashboardController(InstructorRepository instructorRepository, 
                           UserRepository userRepository) {
    this.instructorRepository = instructorRepository;
    this.userRepository = userRepository;
  }

  @GetMapping("/instructor")
  public ResponseEntity<ApiResponseDto<InstructorDashboardResponse>> getInstructorDashboard() {
    // Lấy thông tin instructor hiện tại
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));
        
    if (user.getInstructor() == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponseDto.<InstructorDashboardResponse>builder()
              .status(403)
              .message("Không có quyền truy cập")
              .response(null)
              .build());
    }

    int instructorId = user.getInstructor().getId();

    // Tính toán các chỉ số
    long totalCourses = instructorRepository.countCoursesByInstructor(instructorId);
    long totalStudents = instructorRepository.countEnrollmentsByInstructor(instructorId);
    long totalSubscribers = instructorRepository.countSubscribersByInstructor(instructorId);
    double totalRevenue = instructorRepository.calculateTotalRevenue(instructorId) != null ? 
                         instructorRepository.calculateTotalRevenue(instructorId) : 0.0;
    double averageRating = instructorRepository.calculateAverageRating(instructorId) != null ?
                          instructorRepository.calculateAverageRating(instructorId) : 0.0;

    InstructorDashboardResponse response = InstructorDashboardResponse.builder()
        .totalCourses(totalCourses)
        .totalStudents(totalStudents)
        .totalSubscribers(totalSubscribers) 
        .totalRevenue(totalRevenue)
        .averageRating(averageRating)
        .build();

    return ResponseEntity.ok(
        ApiResponseDto.<InstructorDashboardResponse>builder()
            .status(200)
            .message("Lấy dữ liệu dashboard thành công")
            .response(response)
            .build());
  }

  @GetMapping("/student")
  public ResponseEntity<ApiResponseDto<StudentDashboardResponse>> viewStudentDashboard() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      // Kiểm tra vai trò
      boolean isStudent =
          authentication.getAuthorities().stream()
              .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
      if (isStudent) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                ApiResponseDto.<StudentDashboardResponse>builder() // Sửa ở đây
                    .status(200)
                    .message(HttpStatus.OK.getReasonPhrase())
                    .response(new StudentDashboardResponse(10, 15, 20))
                    .build());
      }
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(
              ApiResponseDto.<StudentDashboardResponse>builder() // Sửa ở đây
                  .status(403)
                  .message(HttpStatus.FORBIDDEN.getReasonPhrase())
                  .response(null)
                  .build());
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(
            ApiResponseDto.<StudentDashboardResponse>builder() // Sửa ở đây
                .status(403)
                .message(HttpStatus.FORBIDDEN.getReasonPhrase())
                .response(null)
                .build());
  }
}
