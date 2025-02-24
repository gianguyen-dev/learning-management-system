package com.group1.MockProject.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group1.MockProject.dto.request.ReSubmitCourseRequest;
import com.group1.MockProject.exception.GlobalExceptionHandler;
import com.group1.MockProject.service.ReSubmitCourseService;
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
class ReSubmitCourseControllerTest {

  private MockMvc mockMvc;

  @Mock private ReSubmitCourseService reSubmitCourseService;

  @InjectMocks private ReSubmitCourseController reSubmitCourseController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    reSubmitCourseController = new ReSubmitCourseController(reSubmitCourseService);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(reSubmitCourseController)
            .alwaysDo(print())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  public void testResubmitCourse_Success() throws Exception {
    ReSubmitCourseRequest request = new ReSubmitCourseRequest();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/course/re-submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  public void testResubmitCourse_MethodNotAllowed() throws Exception {
    ReSubmitCourseRequest request = new ReSubmitCourseRequest();

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/course/re-submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isMethodNotAllowed());
  }

  @Test
  public void testResubmitCourse_NoHandlerFoundException() throws Exception {
    ReSubmitCourseRequest request = new ReSubmitCourseRequest();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/course/re-submit/invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isNotFound());
  }
}
