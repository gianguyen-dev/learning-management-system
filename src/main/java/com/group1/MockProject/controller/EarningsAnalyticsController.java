package com.group1.MockProject.controller;

import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.response.EarningsAnalyticsResponse;
import com.group1.MockProject.service.EarningsAnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/earnings")
public class EarningsAnalyticsController {

  private final EarningsAnalyticsService earningsAnalyticsService;

  public EarningsAnalyticsController(EarningsAnalyticsService earningsAnalyticsService) {
    this.earningsAnalyticsService = earningsAnalyticsService;
  }

  @GetMapping
  public ResponseEntity<ApiResponseDto<EarningsAnalyticsResponse>> viewEarningsAnalytics() {
    EarningsAnalyticsResponse response = earningsAnalyticsService.getEarningsData();
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<EarningsAnalyticsResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }
}
