package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.ForgotPasswordRequest;
import com.group1.MockProject.dto.request.ResetPasswordRequest;
import com.group1.MockProject.dto.request.SignInRequest;
import com.group1.MockProject.dto.request.SignUpRequest;
import com.group1.MockProject.dto.request.SignUpRequestForInstructor;
import com.group1.MockProject.dto.response.*;
import com.group1.MockProject.entity.ConfirmToken;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.entity.UserRole;
import com.group1.MockProject.repository.ConfirmTokenRepository;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.StudentRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.utils.JwtUtil;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private StudentRepository studentRepository;

  @Mock InstructorRepository instructorRepository;

  @Mock private ConfirmTokenRepository confirmTokenRepository;

  @Mock private ConfirmTokenServiceImpl confirmTokenService;

  @Mock private EmailServiceImpl emailService;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private AuthServiceImpl authService;

  private User mockUser;
  private SignUpRequest signUpRequest;
  private SignInRequest signInRequest;
  private SignUpRequestForInstructor signUpRequestForInstructor;
  private ForgotPasswordRequest forgotPasswordRequest;
  private ResetPasswordRequest resetPasswordRequest;
  private String rawPassword;
  private String hashedPassword;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    rawPassword = "password";
    hashedPassword = "hashedPassword";

    mockUser = new User();
    mockUser.setEmail("test@test.com");
    mockUser.setPassword(hashedPassword);
    mockUser.setFullName("Test");
    mockUser.setAddress("Address");
    mockUser.setPhone("0909111000");
    mockUser.setStatus(0);
    mockUser.setRole(UserRole.STUDENT);
  }

  @Test
  public void testRegister_Success() {
    String mockToken = "mockToken";

    signUpRequest = new SignUpRequest();
    signUpRequest.setEmail(mockUser.getEmail());
    signUpRequest.setFullName(mockUser.getFullName());
    signUpRequest.setAddress(mockUser.getAddress());
    signUpRequest.setPhone(mockUser.getPhone());
    signUpRequest.setPassword(rawPassword);
    signUpRequest.setConfirmPassword(rawPassword);

    Mockito.when(userRepository.findByEmail(signUpRequest.getEmail())).thenReturn(Optional.empty());
    Mockito.when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(mockUser);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.generateToken(mockUser)).thenReturn(mockToken);

      SignUpResponse result = authService.signUp(signUpRequest);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(mockToken, result.getToken());
      Mockito.verify(passwordEncoder, Mockito.times(1)).encode(rawPassword);
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }
  }

  @Test
  public void testRegister_UserAlreadyExists() {
    signUpRequest = new SignUpRequest();
    signUpRequest.setEmail(mockUser.getEmail());

    Mockito.when(userRepository.findByEmail(signUpRequest.getEmail()))
        .thenReturn(Optional.of(mockUser));

    Exception exception =
        Assertions.assertThrows(
            DataIntegrityViolationException.class, () -> authService.signUp(signUpRequest));

    Assertions.assertEquals("Người dùng đã tồn tại trong hệ thống", exception.getMessage());
  }

  @Test
  public void testRegister_ConfirmPasswordNotMatch() {
    signUpRequest = new SignUpRequest();
    signUpRequest.setPassword(rawPassword);
    signUpRequest.setConfirmPassword("wrongPassword");

    Exception exception =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> authService.signUp(signUpRequest));

    Assertions.assertEquals("Mật khẩu không trùng khớp", exception.getMessage());
  }

  @Test
  public void testAuthenticate_Success() {
    String mockToken = "mockToken";

    signInRequest = new SignInRequest();
    signInRequest.setEmail(mockUser.getEmail());
    signInRequest.setPassword(rawPassword);

    Mockito.when(userRepository.findByEmail(signInRequest.getEmail()))
        .thenReturn(Optional.of(mockUser));
    Mockito.when(passwordEncoder.matches(signInRequest.getPassword(), mockUser.getPassword()))
        .thenReturn(true);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.generateToken(mockUser)).thenReturn(mockToken);

      SignInResponse result = authService.authenticate(signInRequest);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(mockToken, result.getToken());
      Mockito.verify(passwordEncoder, Mockito.times(1))
          .matches(signInRequest.getPassword(), mockUser.getPassword());
    }
  }

  @Test
  public void testAuthenticate_UserNotFound() {
    signInRequest = new SignInRequest();
    signInRequest.setEmail("nonexistent@test.com");
    signInRequest.setPassword(rawPassword);

    Mockito.when(userRepository.findByEmail(signInRequest.getEmail())).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            BadCredentialsException.class, () -> authService.authenticate(signInRequest));

    Assertions.assertEquals("Sai email hoặc mật khẩu", exception.getMessage());
  }

  @Test
  public void testAuthenticate_PasswordNotMatch() {
    signInRequest = new SignInRequest();
    signInRequest.setEmail(mockUser.getEmail());
    signInRequest.setPassword("wrongPassword");

    Mockito.when(userRepository.findByEmail(signInRequest.getEmail()))
        .thenReturn(Optional.of(mockUser));
    Mockito.when(passwordEncoder.matches(signInRequest.getPassword(), mockUser.getPassword()))
        .thenReturn(false);

    Exception exception =
        Assertions.assertThrows(
            BadCredentialsException.class, () -> authService.authenticate(signInRequest));

    Assertions.assertEquals("Sai email hoặc mật khẩu", exception.getMessage());
  }

  @Test
  public void testAuthenticate_UserIsBlocked() {
    signInRequest = new SignInRequest();
    signInRequest.setEmail(mockUser.getEmail());
    signInRequest.setPassword(rawPassword);

    mockUser.setStatus(-1);

    Mockito.when(userRepository.findByEmail(signInRequest.getEmail()))
        .thenReturn(Optional.of(mockUser));
    Mockito.when(passwordEncoder.matches(signInRequest.getPassword(), mockUser.getPassword()))
        .thenReturn(true);

    Exception exception =
        Assertions.assertThrows(
            AccessDeniedException.class, () -> authService.authenticate(signInRequest));

    Assertions.assertEquals("Tài khoản đã bị khóa", exception.getMessage());
  }

  @Test
  public void testRegisterForInstructor_Success() {
    String mockToken = "mockToken";

    signUpRequestForInstructor = new SignUpRequestForInstructor();
    signUpRequestForInstructor.setEmail(mockUser.getEmail());
    signUpRequestForInstructor.setPassword(rawPassword);
    signUpRequestForInstructor.setConfirmPassword(rawPassword);

    Mockito.when(userRepository.findByEmail(signUpRequestForInstructor.getEmail()))
        .thenReturn(Optional.empty());
    Mockito.when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(mockUser);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.generateToken(mockUser)).thenReturn(mockToken);

      SignUpResponse result = authService.signUpForInstructor(signUpRequestForInstructor);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(mockToken, result.getToken());
      Mockito.verify(passwordEncoder, Mockito.times(1)).encode(rawPassword);
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }
  }

  @Test
  public void testRegisterForInstructor_UserAlreadyExists() {
    signUpRequestForInstructor = new SignUpRequestForInstructor();
    signUpRequestForInstructor.setEmail(mockUser.getEmail());

    Mockito.when(userRepository.findByEmail(signUpRequestForInstructor.getEmail()))
        .thenReturn(Optional.of(mockUser));

    Exception exception =
        Assertions.assertThrows(
            DataIntegrityViolationException.class,
            () -> authService.signUpForInstructor(signUpRequestForInstructor));

    Assertions.assertEquals("Người dùng đã tồn tại trong hệ thống", exception.getMessage());
  }

  @Test
  public void testRegisterForInstructor_ConfirmPasswordNotMatch() {
    signUpRequestForInstructor = new SignUpRequestForInstructor();
    signUpRequestForInstructor.setPassword(rawPassword);
    signUpRequestForInstructor.setConfirmPassword("wrongPassword");

    Exception exception =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> authService.signUpForInstructor(signUpRequestForInstructor));

    Assertions.assertEquals("Mật khẩu không trùng khớp", exception.getMessage());
  }

  @Test
  public void testConfirmToken_Success() {
    String mockToken = "mockToken";
    ConfirmToken confirmToken = new ConfirmToken();
    confirmToken.setToken(mockToken);
    confirmToken.setExpiresAt(LocalDateTime.now().plusDays(1));
    confirmToken.setUser(mockUser);

    Mockito.when(confirmTokenService.getToken(mockToken)).thenReturn(Optional.of(confirmToken));

    String result = authService.confirmToken(mockToken);

    Assertions.assertEquals("Xác thực thành công, tài khoản hoạt động.", result);
    Mockito.verify(confirmTokenService, Mockito.times(1)).setConfirmedAt(mockToken);
    Assertions.assertEquals(1, confirmToken.getUser().getStatus());
    Assertions.assertEquals(mockToken, confirmToken.getUser().getVerificationCode());
  }

  @Test
  public void testConfirmToken_TokenNotFound() {
    String mockToken = "invalid_token";

    Mockito.when(confirmTokenService.getToken(mockToken)).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            BadCredentialsException.class, () -> authService.confirmToken(mockToken));
    Assertions.assertEquals("Không tìm thấy Token", exception.getMessage());
  }

  @Test
  public void testConfirmToken_TokenAlreadyConfirmed() {
    String mockToken = "already_confirmed_token";
    ConfirmToken confirmToken = new ConfirmToken();
    confirmToken.setToken(mockToken);
    confirmToken.setConfirmedAt(LocalDateTime.now());

    Mockito.when(confirmTokenService.getToken(mockToken)).thenReturn(Optional.of(confirmToken));

    Exception exception =
        Assertions.assertThrows(
            DataIntegrityViolationException.class, () -> authService.confirmToken(mockToken));
    Assertions.assertEquals("Email đã được xác nhận", exception.getMessage());
  }

  @Test
  public void testConfirmToken_TokenAlreadyExpired() {
    String mockToken = "already_expired_token";
    ConfirmToken confirmToken = new ConfirmToken();
    confirmToken.setToken(mockToken);
    confirmToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));

    Mockito.when(confirmTokenService.getToken(mockToken)).thenReturn(Optional.of(confirmToken));

    Exception exception =
        Assertions.assertThrows(
            BadCredentialsException.class, () -> authService.confirmToken(mockToken));
    Assertions.assertEquals("Token đã hết hạn", exception.getMessage());
  }

  @Test
  public void testChangePassword_Success() {
    String oldPassword = rawPassword;
    String mockToken = "mockToken";

    Mockito.when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
    Mockito.when(passwordEncoder.matches(oldPassword, mockUser.getPassword())).thenReturn(true);
    Mockito.when(passwordEncoder.encode("newPassword")).thenReturn("hashedNewPassword");

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(Mockito.anyString())).thenReturn(true);
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.anyString()))
          .thenReturn(mockUser.getEmail());

      authService.changePassword(mockToken, oldPassword, "newPassword");

      Assertions.assertEquals("hashedNewPassword", mockUser.getPassword());
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }
  }

  @Test
  public void testChangePassword_UserNotFound() {
    String mockToken = "mockToken";
    String oldPassword = rawPassword;

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(Mockito.anyString())).thenReturn(true);
      jwtUtilMockedStatic.when(() -> JwtUtil.extractEmail(Mockito.anyString())).thenReturn(null);

      Exception exception =
          Assertions.assertThrows(
              EmptyResultDataAccessException.class,
              () -> authService.changePassword(mockToken, oldPassword, "newPassword"));
      Assertions.assertEquals("Người dùng không tồn tại trong hệ thống", exception.getMessage());
    }
  }

  @Test
  public void testChangePassword_InvalidOldPassword() {
    String mockToken = "mockToken";
    String oldPassword = "invalid_old_password";

    Mockito.when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
    Mockito.when(passwordEncoder.matches(oldPassword, mockUser.getPassword())).thenReturn(false);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(Mockito.anyString())).thenReturn(true);
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.anyString()))
          .thenReturn(mockUser.getEmail());

      Exception exception =
          Assertions.assertThrows(
              BadCredentialsException.class,
              () -> authService.changePassword(mockUser.getEmail(), oldPassword, "newPassword"));
      Assertions.assertEquals("Mật khẩu cũ không đúng", exception.getMessage());
    }
  }

  @Test
  public void testChangePassword_InvalidToken() {
    String mockToken = "invalidMockToken";
    String oldPassword = rawPassword;

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(Mockito.anyString())).thenReturn(false);
      jwtUtilMockedStatic.when(() -> JwtUtil.extractEmail(Mockito.anyString())).thenReturn(null);

      Exception exception =
          Assertions.assertThrows(
              BadCredentialsException.class,
              () -> authService.changePassword(mockToken, oldPassword, "newPassword"));
      Assertions.assertEquals("Token không hợp lệ hoặc đã hết hạn", exception.getMessage());
    }
  }

  @Test
  public void testForgotPassword_Success() {
    String mockToken = "mockToken";

    forgotPasswordRequest = new ForgotPasswordRequest();
    forgotPasswordRequest.setEmail(mockUser.getEmail());

    Mockito.when(userRepository.findByEmail(forgotPasswordRequest.getEmail()))
        .thenReturn(Optional.of(mockUser));

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.generateToken(mockUser)).thenReturn(mockToken);

      ForgotPasswordResponse result = authService.forgotPassword(forgotPasswordRequest);

      Assertions.assertNotNull(result);
      Assertions.assertEquals(mockToken, Objects.requireNonNull(result.getToken()));
    }
  }

  @Test
  public void testForgotPassword_UserNotFound() {
    forgotPasswordRequest = new ForgotPasswordRequest();
    forgotPasswordRequest.setEmail("nonexistent_email@gmail.com");

    Mockito.when(userRepository.findByEmail(forgotPasswordRequest.getEmail()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> authService.forgotPassword(forgotPasswordRequest));
    Assertions.assertEquals("Người dùng không tồn tại trong hệ thống", exception.getMessage());
  }

  @Test
  public void testResetPassword_Success() {
    String mockToken = "mockToken";
    String newPassword = "newPassword";

    resetPasswordRequest = new ResetPasswordRequest();
    resetPasswordRequest.setToken(mockToken);
    resetPasswordRequest.setNewPassword(newPassword);
    resetPasswordRequest.setConfirmPassword(newPassword);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(mockToken)).thenReturn(true);
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(mockToken))
          .thenReturn(mockUser.getEmail());

      Mockito.when(userRepository.findByEmail(mockUser.getEmail()))
          .thenReturn(Optional.of(mockUser));
      Mockito.when(passwordEncoder.encode(newPassword)).thenReturn("hashedNewPassword");

      ResetPasswordResponse result = authService.resetPassword(resetPasswordRequest);

      Assertions.assertEquals(
          "Đổi mật khẩu thành công", Objects.requireNonNull(result.getMessage()));
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
      Assertions.assertEquals("hashedNewPassword", mockUser.getPassword());
    }
  }

  @Test
  public void testResetPassword_UserNotFound() {
    String mockToken = "mockToken";

    resetPasswordRequest = new ResetPasswordRequest();
    resetPasswordRequest.setToken("mockToken");

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(mockToken)).thenReturn(true);
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(mockToken))
          .thenReturn("nonexistent_email@gmail.com");

      Mockito.when(userRepository.findByEmail("nonexistent_email@gmail.com"))
          .thenReturn(Optional.empty());

      Exception exception =
          Assertions.assertThrows(
              EmptyResultDataAccessException.class,
              () -> authService.resetPassword(resetPasswordRequest));
      Assertions.assertEquals("Người dùng không tồn tại trong hệ thống", exception.getMessage());
    }
  }

  @Test
  public void testResetPassword_InvalidToken() {
    String mockToken = "mockToken";

    resetPasswordRequest = new ResetPasswordRequest();
    resetPasswordRequest.setToken(mockToken);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(mockToken)).thenReturn(false);

      Exception exception =
          Assertions.assertThrows(
              BadCredentialsException.class, () -> authService.resetPassword(resetPasswordRequest));
      Assertions.assertEquals(
          "Mã tái thiết lập không hợp lệ hoặc đã hết hạn", exception.getMessage());
    }
  }

  @Test
  public void testResetPassword_ConfirmPasswordNotMatch() {
    String mockToken = "mockToken";
    String newPassword = "newPassword";

    resetPasswordRequest = new ResetPasswordRequest();
    resetPasswordRequest.setToken(mockToken);
    resetPasswordRequest.setNewPassword(newPassword);
    resetPasswordRequest.setConfirmPassword("wrong_confirm_password");

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(mockToken)).thenReturn(true);
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(mockToken))
          .thenReturn(mockUser.getEmail());

      Mockito.when(userRepository.findByEmail(mockUser.getEmail()))
          .thenReturn(Optional.of(mockUser));

      Exception exception =
          Assertions.assertThrows(
              IllegalArgumentException.class,
              () -> authService.resetPassword(resetPasswordRequest));
      Assertions.assertEquals("Mật khẩu không trùng khớp", exception.getMessage());
    }
  }

  @Test
  public void testSignUpOAuth2_NewUser() {
    User newUser = new User();
    newUser.setEmail("nonexistent_email@gmail.com");
    newUser.setFullName("New User");
    newUser.setProvider("google");
    newUser.setStatus(1);

    String mockToken = "mockToken";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.generateToken(Mockito.any(User.class)))
          .thenReturn(mockToken);

      Mockito.when(userRepository.findByEmailAndProvider(newUser.getEmail(), newUser.getProvider()))
          .thenReturn(Optional.empty());
      Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(hashedPassword);
      Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(newUser);

      SignInResponse result =
          authService.signUpOAuth2(
              newUser.getEmail(), newUser.getFullName(), newUser.getProvider(), true);

      Assertions.assertEquals("Đăng nhập với Google thành công", result.getMessage());
      Assertions.assertEquals("Bearer", result.getTokenType());
      Assertions.assertEquals(mockToken, result.getToken());
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }
  }

  @Test
  public void testSignUpOAuth2_UserExistsWithPassword() {
    mockUser.setProvider("google");
    String mockToken = "mockToken";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.generateToken(Mockito.any(User.class)))
          .thenReturn(mockToken);

      Mockito.when(
              userRepository.findByEmailAndProvider(mockUser.getEmail(), mockUser.getProvider()))
          .thenReturn(Optional.of(mockUser));

      SignInResponse result =
          authService.signUpOAuth2(
              mockUser.getEmail(), mockUser.getFullName(), mockUser.getProvider(), true);

      Assertions.assertEquals("Đăng nhập với Google thành công", result.getMessage());
      Assertions.assertEquals("Bearer", result.getTokenType());
      Assertions.assertEquals(mockToken, result.getToken());
    }
  }

  @Test
  public void testSignUpOAuth2_UserExistsWithoutPassword() {
    mockUser.setProvider("google");
    mockUser.setPassword(null);
    String mockToken = "mockToken";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.generateToken(Mockito.any(User.class)))
          .thenReturn(mockToken);

      Mockito.when(
              userRepository.findByEmailAndProvider(mockUser.getEmail(), mockUser.getProvider()))
          .thenReturn(Optional.of(mockUser));
      Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(hashedPassword);

      SignInResponse result =
          authService.signUpOAuth2(
              mockUser.getEmail(), mockUser.getFullName(), mockUser.getProvider(), true);

      Assertions.assertEquals("Đăng nhập với Google thành công", result.getMessage());
      Assertions.assertEquals("Bearer", result.getTokenType());
      Assertions.assertEquals(mockToken, result.getToken());
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }
  }
}
