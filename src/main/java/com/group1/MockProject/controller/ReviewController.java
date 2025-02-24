package com.group1.MockProject.controller;

import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.response.ReviewResponse;
import com.group1.MockProject.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping
  public ResponseEntity<ApiResponseDto<List<ReviewResponse>>> viewReviews() {
    List<ReviewResponse> reviews = reviewService.getAllReviews();
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<List<ReviewResponse>>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(reviews)
                .build());
  }
}
