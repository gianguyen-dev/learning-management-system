package com.group1.MockProject.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.group1.MockProject.exception.GlobalExceptionHandler;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

  private MockMvc mockMvc;

  @Mock private SecurityContextHolder securityContextHolder;

  @InjectMocks private DashboardController dashboardController;

  @BeforeEach
  void setup() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(dashboardController)
            .alwaysDo(print())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  public void testViewInstructorDashboard_Success() throws Exception {

    List<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"));
    Authentication auth = new UsernamePasswordAuthenticationToken("instructor", null, authorities);
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(auth);
    SecurityContextHolder.setContext(securityContext);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/dashboard/instructor")
                .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response.totalStudents").value(100))
        .andExpect(jsonPath("$.response.totalCourses").value(100));
  }

  @Test
  public void testViewInstructorDashboard_NotHaveAccess() throws Exception {

    List<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));
    Authentication auth = new UsernamePasswordAuthenticationToken("student", null, authorities);
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(auth);
    SecurityContextHolder.setContext(securityContext);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/dashboard/instructor")
                .contentType("application/json"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403))
        .andExpect(jsonPath("$.message").value("Forbidden"));
  }

  @Test
  public void testViewInstructorDashboard_Unauthorized() throws Exception {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(null);
    SecurityContextHolder.setContext(securityContext);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/dashboard/instructor")
                .contentType("application/json"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Unauthorized"));
  }

  @Test
  public void testViewStudentDashboard_Success() throws Exception {

    List<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));
    Authentication auth = new UsernamePasswordAuthenticationToken("student", null, authorities);
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(auth);
    SecurityContextHolder.setContext(securityContext);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/dashboard/student").contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response.totalCourses").value(10))
        .andExpect(jsonPath("$.response.totalEnrollments").value(15))
        .andExpect(jsonPath("$.response.totalPurchasedCourses").value(20));
  }

  @Test
  public void testViewStudentDashboard_NotHaveAccess() throws Exception {

    List<GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"));
    Authentication auth = new UsernamePasswordAuthenticationToken("instructor", null, authorities);
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(auth);
    SecurityContextHolder.setContext(securityContext);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/dashboard/student").contentType("application/json"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value(403))
        .andExpect(jsonPath("$.message").value("Forbidden"));
  }

  @Test
  public void testViewStudentDashboard_Unauthorized() throws Exception {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(null);
    SecurityContextHolder.setContext(securityContext);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/dashboard/student").contentType("application/json"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Unauthorized"));
  }
}
