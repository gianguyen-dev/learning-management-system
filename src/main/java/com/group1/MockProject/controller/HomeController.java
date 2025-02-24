package com.group1.MockProject.controller;

import com.group1.MockProject.dto.AnalyticDTO;
import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.MessageDTO;
import com.group1.MockProject.dto.request.OTPRequest;
import com.group1.MockProject.dto.request.UpdateProfileRequest;
import com.group1.MockProject.dto.response.GuestHomePageDTO;
import com.group1.MockProject.dto.response.StudentHomePageDTO;
import com.group1.MockProject.dto.response.UpdateProfileResponse;
import com.group1.MockProject.dto.response.UserInfoResponse;
import com.group1.MockProject.entity.Analytic;
import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Instructor;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.service.AnalyticService;
import com.group1.MockProject.service.CourseService;
import com.group1.MockProject.service.HomePageService;
import com.group1.MockProject.service.UserService;
import com.group1.MockProject.utils.JwtUtil;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class HomeController {

  private CourseService courseService;
  private UserService userService;
  private HomePageService homePageService;
  private AnalyticService analyticService;

  public HomeController(CourseService courseService, UserService userService, HomePageService homePageService, AnalyticService analyticService) {
    this.courseService = courseService;
    this.userService = userService;
    this.homePageService = homePageService;
    this.analyticService = analyticService;
  }

  @GetMapping({"/", "/index", "/home", "", "/homepage", "/home-page"})
  public ResponseEntity<ApiResponseDto<?>> index(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      String email = authentication.getName();
      Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

      if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT"))) {

        StudentHomePageDTO response = homePageService.getHomePageForStudent(email);

        // return homepage for student
        return ResponseEntity.ok()
            .body(
                ApiResponseDto.<StudentHomePageDTO>builder()
                    .status(200)
                    .message(HttpStatus.OK.getReasonPhrase())
                    .response(response)
                    .build());
      } else if (authorities.stream()
          .anyMatch(auth -> auth.getAuthority().equals("ROLE_INSTRUCTOR"))) {
        // return homepage for instructor
        return ResponseEntity.ok()
            .body(
                ApiResponseDto.<MessageDTO>builder()
                    .status(200)
                    .message(HttpStatus.OK.getReasonPhrase())
                    .response(new MessageDTO("Welcome to the INSTRUCTOR Homepage!"))
                    .build());
      }
    }

    GuestHomePageDTO response = homePageService.getHomePageForGuest();
    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<GuestHomePageDTO>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
  @GetMapping("/profile")
  public ResponseEntity<ApiResponseDto<UserInfoResponse>> getProfile(
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");

    UserInfoResponse response = userService.getUserInfoByToken(token);

    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<UserInfoResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  @GetMapping("/admin")
  public String admin() {

    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();
    return authentication.toString();
  }

  // POST /api/v1/profile - Enter email to receive email confirm update profile
  @PostMapping("/profile")
  public ResponseEntity<ApiResponseDto> requestProfileUpdate(
      @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");

    UpdateProfileResponse response = userService.requestProfileUpdate(token);

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(
            ApiResponseDto.<UpdateProfileResponse>builder()
                .status(202)
                .message(HttpStatus.ACCEPTED.getReasonPhrase())
                .response(response)
                .build());
  }

  // GET /api/v1/profile/confirm-update
  @PutMapping("/profile/confirm-update")
  public ResponseEntity<ApiResponseDto<?>> confirmUpdateProfile(@RequestBody @Valid UpdateProfileRequest request,
                                                             @RequestHeader("Authorization") String authorizationHeader) {

    String token = authorizationHeader.replace("Bearer ", "");
    String response = userService.updateUserInfo(token, request);

    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<String>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }
  @GetMapping("/analytic")
  public ResponseEntity<?> instructorAnalytic(
          @RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.replace("Bearer ", "");
    String email = JwtUtil.extractEmail(token);
    AnalyticDTO.AnalyticResponse response = analyticService.getInstructorAnalytic(email);
    return ResponseEntity.ok()
            .body(
                    ApiResponseDto.<AnalyticDTO.AnalyticResponse>builder()
                            .status(200)
                            .message(HttpStatus.OK.getReasonPhrase())
                            .response(response)
                            .build());
  }
}
