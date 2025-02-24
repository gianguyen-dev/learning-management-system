package com.group1.MockProject.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.request.*;
import com.group1.MockProject.dto.response.ForgotPasswordResponse;
import com.group1.MockProject.dto.response.ResetPasswordResponse;
import com.group1.MockProject.dto.response.SignInResponse;
import com.group1.MockProject.dto.response.SignUpResponse;
import com.group1.MockProject.exception.GlobalExceptionHandler;
import com.group1.MockProject.service.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

  private MockMvc mockMvc;

  @Mock private AuthService authService;

  @InjectMocks private AuthenticationController authenticationController;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    authenticationController = new AuthenticationController(authService);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(authenticationController)
            .alwaysDo(print())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  public void testSignIn_Success() throws Exception {

    SignInRequest signInRequest = new SignInRequest();
    signInRequest.setEmail("email@email.com");
    signInRequest.setPassword("password");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(signInRequest);

    SignInResponse signInResponse = new SignInResponse();
    Mockito.when(authService.authenticate(Mockito.any(SignInRequest.class)))
        .thenReturn(signInResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testSignIn_PasswordLessThan6Characters() throws Exception {

    SignInRequest signInRequest = new SignInRequest();
    signInRequest.setEmail("email@email.com");
    signInRequest.setPassword("pass");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(signInRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Bad Request"))
        .andExpect(jsonPath("$.response.message").value("Lỗi xác thực"));
  }

  @Test
  public void testSignUp_Success() throws Exception {
    SignUpRequest signUpRequest = new SignUpRequest();
    signUpRequest.setEmail("email@email.com");
    signUpRequest.setPassword("password");
    signUpRequest.setFullName("Le Dinh Khoi");
    signUpRequest.setPhone("0909100100");
    signUpRequest.setConfirmPassword("password");
    signUpRequest.setAddress("Address");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(signUpRequest);

    SignUpResponse signUpResponse = new SignUpResponse();
    Mockito.when(authService.signUp(Mockito.any(SignUpRequest.class))).thenReturn(signUpResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(201))
        .andExpect(jsonPath("$.message").value("Created"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testConfirmSignUp_Success() throws Exception {
    String expectedResult = "Expected Result";

    String mockToken = "Mock Token";

    Mockito.when(authService.confirmToken(Mockito.eq(mockToken))).thenReturn(expectedResult);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/auth/sign-up/confirm")
                .param("token", mockToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedResult));
  }

  @Test
  public void testChangePassword_Success() throws Exception {
    String mockToken = "Mock Token";
    String expectedResult = "Expected Result";

    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
    changePasswordRequest.setNewPassword("newPassword");
    changePasswordRequest.setOldPassword("oldPassword");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(changePasswordRequest);

    ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
    Mockito.when(
            authService.changePassword(
                Mockito.eq(mockToken),
                Mockito.eq(changePasswordRequest.getOldPassword()),
                Mockito.eq(changePasswordRequest.getNewPassword())))
        .thenReturn(resetPasswordResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer " + mockToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testChangePassword_InvalidAuthorizationHeader() throws Exception {
    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
    changePasswordRequest.setNewPassword("newPassword");
    changePasswordRequest.setOldPassword("oldPassword");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(changePasswordRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Unauthorized"))
        .andExpect(jsonPath("$.response.message").value("Token không hợp lệ"));
  }

  @Test
  public void testLoginOauth2Success_Success() throws Exception {
    String email = "email@gmail.com";
    String name = "fullName";
    boolean status = true;
    String provider = "google";

    OAuth2AuthenticationToken authenticationToken = Mockito.mock(OAuth2AuthenticationToken.class);
    Authentication authentication = Mockito.mock(Authentication.class);
    OAuth2User oAuth2User = Mockito.mock(OAuth2User.class);

    Mockito.when(authentication.getPrincipal()).thenReturn(oAuth2User);
    Mockito.when(oAuth2User.getAttribute("email")).thenReturn(email);
    Mockito.when(oAuth2User.getAttribute("name")).thenReturn(name);
    Mockito.when(oAuth2User.getAttribute("email_verified")).thenReturn(status);
    Mockito.when(authenticationToken.getAuthorizedClientRegistrationId()).thenReturn(provider);

    SignInResponse signInResponse = new SignInResponse();

    Mockito.when(
            authService.signUpOAuth2(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyBoolean()))
        .thenReturn(signInResponse);

    ResponseEntity<ApiResponseDto<SignInResponse>> response =
        authenticationController.loginOauth2Success(authentication, authenticationToken);

    // Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(200, response.getBody().getStatus());
    Assertions.assertEquals(HttpStatus.OK.getReasonPhrase(), response.getBody().getMessage());
    Assertions.assertEquals(signInResponse, response.getBody().getResponse());
  }

  @Test
  public void testLoginOauth2Success_NullAuthenticationToken() {
    Authentication authentication = Mockito.mock(Authentication.class);

    // Act
    ResponseEntity<ApiResponseDto<SignInResponse>> response =
        authenticationController.loginOauth2Success(authentication, null);

    // Assert
    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    Mockito.verifyNoInteractions(authService);
  }

  @Test
  public void testForgotPassword_Success() throws Exception {
    ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
    forgotPasswordRequest.setEmail("email@email.com");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(forgotPasswordRequest);

    ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse();
    Mockito.when(authService.forgotPassword(Mockito.any(ForgotPasswordRequest.class)))
        .thenReturn(forgotPasswordResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.status").value(202))
        .andExpect(jsonPath("$.message").value("Accepted"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testForgotPassword_HasBindingError() throws Exception {
    BindingResult bindingResult = Mockito.mock(BindingResult.class);
    ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
    forgotPasswordRequest.setEmail("email@email.com");

    Mockito.when(bindingResult.hasErrors()).thenReturn(true);

    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> authenticationController.forgotPassword(forgotPasswordRequest, bindingResult));

    Assertions.assertEquals("Vui lòng nhập đầy đủ thông tin", exception.getMessage());
    Mockito.verify(authService, Mockito.never()).forgotPassword(Mockito.any());
  }

  @Test
  public void testResetPassword_Success() throws Exception {
    ChangeForgotPasswordRequest changePasswordRequest = new ChangeForgotPasswordRequest();
    changePasswordRequest.setNewPassword("newPassword");
    changePasswordRequest.setConfirmPassword("confirmPassword");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(changePasswordRequest);

    ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
    Mockito.when(authService.resetPassword(Mockito.any(ResetPasswordRequest.class)))
        .thenReturn(resetPasswordResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/forgot-password/change")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer validToken")
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testResetPassword_InvalidToken() throws Exception {
    ChangeForgotPasswordRequest changePasswordRequest = new ChangeForgotPasswordRequest();
    changePasswordRequest.setNewPassword("newPassword");
    changePasswordRequest.setConfirmPassword("confirmPassword");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(changePasswordRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/forgot-password/change")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.message").value("Unauthorized"));
  }

  @Test
  public void testResetPassword_MissingInput() throws Exception {
    ChangeForgotPasswordRequest changePasswordRequest = new ChangeForgotPasswordRequest();
    changePasswordRequest.setNewPassword("newPassword");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(changePasswordRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/forgot-password/change")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer validToken")
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Bad Request"));
  }

  @Test
  public void testSignUpForInstructor_Success() throws Exception {
    SignUpRequestForInstructor signUpRequest = new SignUpRequestForInstructor();
    signUpRequest.setEmail("instructor@email.com");
    signUpRequest.setPassword("password");
    signUpRequest.setFullName("Le Dinh Khoi");
    signUpRequest.setPhone("0909100100");
    signUpRequest.setConfirmPassword("password");
    signUpRequest.setAddress("Address");
    signUpRequest.setExpertise("Expertise");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(signUpRequest);

    SignUpResponse signUpResponse = new SignUpResponse();
    Mockito.when(authService.signUpForInstructor(Mockito.any(SignUpRequestForInstructor.class)))
        .thenReturn(signUpResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-up/instructor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(201))
        .andExpect(jsonPath("$.message").value("Created"))
        .andExpect(jsonPath("$.response").exists());
  }

  @Test
  public void testSignInForInstructor_Success() throws Exception {

    SignInRequest signInRequest = new SignInRequest();
    signInRequest.setEmail("instructor@email.com");
    signInRequest.setPassword("password");

    objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(signInRequest);

    SignInResponse signInResponse = new SignInResponse();
    Mockito.when(authService.authenticate(Mockito.any(SignInRequest.class)))
        .thenReturn(signInResponse);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/auth/sign-in/instructor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("OK"))
        .andExpect(jsonPath("$.response").exists());
  }
}
