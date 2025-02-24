package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.UpdateProfileRequest;
import com.group1.MockProject.dto.response.UpdateProfileResponse;
import com.group1.MockProject.dto.response.UserInfoResponse;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.entity.UserRole;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.service.EmailService;
import com.group1.MockProject.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
  @Mock private UserRepository userRepository;

  @Mock private ModelMapper mapper;

  @Mock private EmailService emailService;

  @InjectMocks private UserServiceImpl userService;

  private User mockUser;
  private UserInfoResponse mockUserInfoResponse;

  @Value("${APPLICATION_HOST}")
  private String HOST;

  @BeforeEach
  void setUp() {
    mockUser = new User();
    mockUser.setFullName("Test User");
    mockUser.setEmail("test@test.com");
    mockUser.setPassword("password");
    mockUser.setStatus(1);
    mockUser.setAddress("Test Address");
    mockUser.setPhone("0909999111");
    mockUser.setRole(UserRole.STUDENT);

    mockUserInfoResponse = new UserInfoResponse();
    mockUserInfoResponse.setEmail("test@test.com");
    mockUserInfoResponse.setFullName("Test User");
    mockUserInfoResponse.setPhone("0909999111");
    mockUserInfoResponse.setAddress("Test Address");
  }

  @Test
  public void testGetUserInfoByToken_Success() {
    String mockToken = "mockToken";
    String userEmail = "test@test.com";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(userEmail);

      Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));

      Mockito.when(
              mapper.map(Mockito.eq(Optional.of(mockUser)), Mockito.eq(UserInfoResponse.class)))
          .thenReturn(mockUserInfoResponse);

      UserInfoResponse result = userService.getUserInfoByToken(mockToken);

      Assertions.assertNotNull(result);
      Mockito.verify(userRepository, Mockito.times(1)).findByEmail(Mockito.eq(userEmail));
    }
  }

  @Test
  public void testGetUserInfoByToken_NoUserFound() {
    String mockToken = "mockToken";
    String userEmail = "invalid@test.com";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(userEmail);

      Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

      Exception exception =
          Assertions.assertThrows(
              EmptyResultDataAccessException.class,
              () -> userService.getUserInfoByToken(mockToken));
      Assertions.assertEquals("Người dùng không tồn tại", exception.getMessage());
    }
  }

  @Test
  public void testRequestProfileUpdate_Success() {
    String mockToken = "mockToken";
    String userEmail = "invalid@test.com";
    String updatedToken = "updatedToken";
    String builtEmail = "builtEmail";

    UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
    updateProfileRequest.setFullName("Test User");
    updateProfileRequest.setAddress("Test Address");
    updateProfileRequest.setPhone("0909999111");

    UpdateProfileResponse updateProfileResponse = new UpdateProfileResponse();
    updateProfileResponse.setLink("Cập nhật thông tin cá nhân");
    updateProfileResponse.setMessage("mockMessage");

    String expectedLink = HOST + "/api/v1/profile/confirm-update?token=" + updatedToken;

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(userEmail);
      Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
      jwtUtilMockedStatic
          .when(
              () ->
                  JwtUtil.generateUpdateUserToken(
                      Mockito.eq(userEmail), Mockito.eq(updateProfileRequest)))
          .thenReturn(updatedToken);
      Mockito.when(
              emailService.buildEmail(
                  Mockito.anyString(), Mockito.eq(mockUser.getEmail()), Mockito.anyString()))
          .thenReturn(builtEmail);
      Mockito.doNothing()
          .when(emailService)
          .sendDetail(Mockito.eq(mockUser.getEmail()), Mockito.eq(builtEmail), Mockito.anyString());

      UpdateProfileResponse result =
          userService.requestProfileUpdate(mockToken, updateProfileRequest);

      Assertions.assertNotNull(result);
      Assertions.assertEquals("Đã gửi link cập nhật profile thành công", result.getMessage());
      Assertions.assertEquals(expectedLink, result.getLink());

      Mockito.verify(emailService, Mockito.times(1))
          .sendDetail(Mockito.eq(mockUser.getEmail()), Mockito.eq(builtEmail), Mockito.anyString());
    }
  }

  @Test
  public void testUpdateUserInfo_Success() {
    String mockToken = "mockToken";

    Claims claims = Mockito.mock(Claims.class);
    claims.put("fullName", "Test User");
    claims.put("address", "Test Address");
    claims.put("phone", "0909999111");
    claims.setSubject(mockUser.getEmail());

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.decodeToken(Mockito.eq(mockToken))).thenReturn(claims);
      Mockito.when(userRepository.findByEmail(Mockito.eq(claims.getSubject())))
          .thenReturn(Optional.of(mockUser));

      Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(mockUser);

      Mockito.when(mapper.map(Mockito.eq(mockUser), Mockito.eq(UserInfoResponse.class)))
          .thenReturn(mockUserInfoResponse);

      UserInfoResponse result = userService.updateUserInfo(mockToken);

      Assertions.assertNotNull(result);

      Mockito.verify(userRepository, Mockito.times(1)).findByEmail(Mockito.eq(claims.getSubject()));
      Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.eq(mockUser));
    }
  }

  @Test
  public void testBlockUserById_Success() {
    String mockToken = "mockToken";
    String userEmail = "invalid@test.com";
    int userId = 1;

    Mockito.when(userRepository.findById(Mockito.eq(userId))).thenReturn(Optional.of(mockUser));

    userService.blockUserById(userId);

    Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
  }

  @Test
  public void testBlockUserById_UserNotFound() {
    String mockToken = "mockToken";
    String userEmail = "invalid@test.com";
    int userId = 1;

    Mockito.when(userRepository.findById(Mockito.eq(userId))).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class, () -> userService.blockUserById(userId));

    Assertions.assertEquals("Không tìm thấy người dùng", exception.getMessage());
  }

  @Test
  public void testUnblockUserById_Success() {
    String mockToken = "mockToken";
    String userEmail = "invalid@test.com";
    int userId = 1;

    Mockito.when(userRepository.findById(Mockito.eq(userId))).thenReturn(Optional.of(mockUser));

    userService.unblockUserById(userId);

    Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
  }

  @Test
  public void testUnblockUserById_UserNotFound() {
    String mockToken = "mockToken";
    String userEmail = "invalid@test.com";
    int userId = 1;

    Mockito.when(userRepository.findById(Mockito.eq(userId))).thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class, () -> userService.unblockUserById(userId));

    Assertions.assertEquals("Không tìm thấy người dùng", exception.getMessage());
  }
}
