package com.group1.MockProject.controller;

import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.request.ReportCourseRequest;
import com.group1.MockProject.service.ReportCourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/report")
public class ReportCourseController {

  private final ReportCourseService reportService;

  public ReportCourseController(ReportCourseService reportService) {
    this.reportService = reportService;
  }

  @PostMapping
  public ResponseEntity<ApiResponseDto<String>> reportCourse(
      @RequestBody ReportCourseRequest request) {
    reportService.reportCourse(request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<String>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(null)
                .build());
  }
}
