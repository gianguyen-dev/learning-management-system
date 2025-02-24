package com.group1.MockProject.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group1.MockProject.dto.request.UpdateProfileRequest;
import com.group1.MockProject.dto.response.GuestHomePageDTO;
import com.group1.MockProject.dto.response.StudentHomePageDTO;
import com.group1.MockProject.dto.response.UpdateProfileResponse;
import com.group1.MockProject.dto.response.UserInfoResponse;
import com.group1.MockProject.exception.GlobalExceptionHandler;
import com.group1.MockProject.service.CourseService;
import com.group1.MockProject.service.EmailService;
import com.group1.MockProject.service.HomePageService;
import com.group1.MockProject.service.UserService;
import com.group1.MockProject.utils.JwtUtil;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {
  private MockMvc mockMvc;

  @Mock private CourseService courseService;

  @Mock private UserService userService;

  @Mock private HomePageService homePageService;

  @Mock private EmailService emailService;

  @InjectMocks private HomeController homeController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private MockedStatic<JwtUtil> jwtUtilMock;

  private final String mockToken = "mockToken";
  private final String mockEmail = "mock@email.com";

  @BeforeEach
  void setUp() {
    jwtUtilMock = Mockito.mockStatic(JwtUtil.class);
    jwtUtilMock.when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken))).thenReturn(mockEmail);
    jwtUtilMock.when(() -> JwtUtil.validateToken(Mockito.eq(mockToken))).thenReturn(true);

    homeController = new HomeController(userService, homePageService, courseService);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(homeController)
            .alwaysDo(print())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @AfterEach
  void tearDown() {
    if (jwtUtilMock != null) {
      jwtUtilMock.close();
    }
  }

  @Test
  public void testGetHomePage_SuccessAsGuest() throws Exception {

    GuestHomePageDTO guestHomePageDTO = new GuestHomePageDTO();
    Mockito.when(homePageService.getHomePageForGuest()).thenReturn(guestHomePageDTO);

    String[] urls = {"/", "/index", "/home", "", "/homepage", "/home-page"};

    for (String url : urls) {
      mockMvc
          .perform(MockMvcRequestBuilders.get("/api/v1" + url).contentType("application/json"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("OK"))
          .andExpect(jsonPath("$.response").exists());
    }
  }

  @Test
  public void testGetHomePage_SuccessAsStudent() throws Exception {
    Authentication authentication = Mockito.mock(Authentication.class);
    Collection<? extends GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));

    Mockito.when(authentication.isAuthenticated()).thenReturn(true);
    Mockito.when(authentication.getName()).thenReturn(mockEmail);
    Mockito.doReturn(authorities).when(authentication).getAuthorities();

    StudentHomePageDTO studentHomePageDTO = new StudentHomePageDTO();
    Mockito.when(homePageService.getHomePageForStudent(authentication.getName()))
        .thenReturn(studentHomePageDTO);

    String[] urls = {"/", "/index", "/home", "", "/homepage", "/home-page"};

    for (String url : urls) {
      mockMvc
          .perform(
              MockMvcRequestBuilders.get("/api/v1" + url)
                  .contentType("application/json")
                  .with(
                      request -> {
                        request.setUserPrincipal(authentication);
                        return request;
                      }))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("OK"))
          .andExpect(jsonPath("$.response").exists());
    }
  }

  @Test
  public void testGetHomePage_SuccessAsInstructor() throws Exception {
    Authentication authentication = Mockito.mock(Authentication.class);
    Collection<? extends GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"));

    Mockito.when(authentication.isAuthenticated()).thenReturn(true);
    Mockito.when(authentication.getName()).thenReturn(mockEmail);
    Mockito.doReturn(authorities).when(authentication).getAuthorities();

    String resultMessage = "Welcome to the INSTRUCTOR Homepage!";

    String[] urls = {"/", "/index", "/home", "", "/homepage", "/home-page"};

    for (String url : urls) {
      mockMvc
          .perform(
              MockMvcRequestBuilders.get("/api/v1" + url)
                  .contentType("application/json")
                  .with(
                      request -> {
                        request.setUserPrincipal(authentication);
                        return request;
                      }))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("OK"))
          .andExpect(jsonPath("$.response.message").value(resultMessage));
    }
  }

  @Test
  public void testGetProfile_Success() throws Exception {
    UserInfoResponse response = new UserInfoResponse();

    Mockito.when(userService.getUserInfoByToken(mockToken)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/profile")
                .contentType("application/json")
                .header("Authorization", "Bearer " + mockToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testAdmin_Success() throws Exception {
    Authentication authentication = Mockito.mock(Authentication.class);
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/admin")
                .contentType("application/json")
                .header("Authorization", "Bearer " + mockToken))
        .andExpect(status().isOk());
  }

  @Test
  public void testRequestProfileUpdate_Success() throws Exception {
    UpdateProfileRequest request = new UpdateProfileRequest();
    request.setPhone("0909111999");
    request.setAddress("Test Address");
    request.setFullName("Test Full Name");

    UpdateProfileResponse response = new UpdateProfileResponse();

    Mockito.when(
            userService.requestProfileUpdate(
                Mockito.eq(mockToken), Mockito.any(UpdateProfileRequest.class)))
        .thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/profile")
                .contentType("application/json")
                .header("Authorization", "Bearer " + mockToken)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.status").value(202))
        .andExpect(jsonPath("$.message").value("Accepted"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testConfirmUpdateProfile_Success() throws Exception {
    UserInfoResponse response = new UserInfoResponse();

    Mockito.when(userService.updateUserInfo(Mockito.eq(mockToken))).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/profile/confirm-update")
                .contentType("application/json")
                .param("token", mockToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }
}
