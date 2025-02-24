package com.group1.MockProject.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.group1.MockProject.exception.GlobalExceptionHandler;
import com.group1.MockProject.service.EarningsAnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class EarningsAnalyticsControllerTest {

  private MockMvc mockMvc;

  @Mock private EarningsAnalyticsService earningsAnalyticsService;

  @InjectMocks private EarningsAnalyticsController earningsAnalyticsController;

  @BeforeEach
  void setUp() {
    earningsAnalyticsController = new EarningsAnalyticsController(earningsAnalyticsService);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(earningsAnalyticsController)
            .alwaysDo(print())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  public void testViewEarningsAnalytics_Success() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/earnings")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk());
  }
}
