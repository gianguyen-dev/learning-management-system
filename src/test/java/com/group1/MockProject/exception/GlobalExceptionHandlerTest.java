package com.group1.MockProject.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.group1.MockProject.controller.TestController;
import com.group1.MockProject.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;

  @Mock private ApplicationContext applicationContext;

  @InjectMocks private TestController testController;

  @BeforeEach
  void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(testController)
            .alwaysDo(print())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void testHandleConstraintViolation() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/test/constraint"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Bad Request")); // Adjust based on your scenario
  }

  @Test
  void testHandleInternalServerError() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/test/runtime"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.message").value("Internal Server Error"));
  }
}
