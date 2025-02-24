package com.group1.MockProject.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.group1.MockProject.exception.GlobalExceptionHandler;
import com.group1.MockProject.service.CourseStudentAnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CourseStudentAnalyticsControllerTest {

  private MockMvc mockMvc;

  @Mock private CourseStudentAnalyticsService courseStudentAnalyticsService;

  @InjectMocks private CourseStudentAnalyticsController courseStudentAnalyticsController;

  @BeforeEach
  void setUp() {
    courseStudentAnalyticsController =
        new CourseStudentAnalyticsController(courseStudentAnalyticsService);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(courseStudentAnalyticsController)
            .alwaysDo(print())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  public void testViewCourseStudentAnalytics_Success() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/course-analytics")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk());
  }
}
