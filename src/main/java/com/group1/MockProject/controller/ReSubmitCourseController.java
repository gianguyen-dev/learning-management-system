package com.group1.MockProject.controller;

import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.request.ReSubmitCourseRequest;
import com.group1.MockProject.service.ReSubmitCourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/course/re-submit")
public class ReSubmitCourseController {

  private final ReSubmitCourseService reSubmitCourseService;

  public ReSubmitCourseController(ReSubmitCourseService reSubmitCourseService) {
    this.reSubmitCourseService = reSubmitCourseService;
  }

  @PostMapping
  public ResponseEntity<ApiResponseDto<String>> reSubmitCourse(
      @RequestBody ReSubmitCourseRequest request) {
    reSubmitCourseService.reSubmitCourse(request);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body(
            ApiResponseDto.<String>builder()
                .status(204)
                .message(HttpStatus.NO_CONTENT.getReasonPhrase())
                .response(null)
                .build());
  }
}
