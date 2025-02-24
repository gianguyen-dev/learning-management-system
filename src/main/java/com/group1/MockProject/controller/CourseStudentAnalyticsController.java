package com.group1.MockProject.controller;

import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.response.CourseStudentAnalyticsResponse;
import com.group1.MockProject.service.CourseStudentAnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/course-analytics")
public class CourseStudentAnalyticsController {

  private final CourseStudentAnalyticsService courseStudentAnalyticsService;

  public CourseStudentAnalyticsController(
      CourseStudentAnalyticsService courseStudentAnalyticsService) {
    this.courseStudentAnalyticsService = courseStudentAnalyticsService;
  }

  @GetMapping
  public ResponseEntity<ApiResponseDto<CourseStudentAnalyticsResponse>>
      viewCourseStudentAnalytics() {
    CourseStudentAnalyticsResponse response = courseStudentAnalyticsService.getCourseStudentData();
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<CourseStudentAnalyticsResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }
}
