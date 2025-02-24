package com.group1.MockProject.controller;

import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.request.ChangeForgotPasswordRequest;
import com.group1.MockProject.dto.request.ChangePasswordRequest;
import com.group1.MockProject.dto.request.ForgotPasswordRequest;
import com.group1.MockProject.dto.request.ResetPasswordRequest;
import com.group1.MockProject.dto.request.SignInRequest;
import com.group1.MockProject.dto.request.SignUpRequest;
import com.group1.MockProject.dto.request.SignUpRequestForInstructor;
import com.group1.MockProject.dto.response.*;
import com.group1.MockProject.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  private AuthService authService;

  public AuthenticationController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/sign-in")
  public ResponseEntity<ApiResponseDto<SignInResponse>> signIn(
      @RequestBody @Valid SignInRequest request) {
    SignInResponse response = authService.authenticate(request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<SignInResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  @PostMapping("/sign-up")
  public ResponseEntity<ApiResponseDto<SignUpResponse>> signUp(
      @RequestBody @Valid SignUpRequest request) {
    SignUpResponse response = authService.signUp(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponseDto.<SignUpResponse>builder()
                .status(201)
                .message(HttpStatus.CREATED.getReasonPhrase())
                .response(response)
                .build());
  }

  @GetMapping("sign-up/confirm")
  public String confirmSignUp(@RequestParam("token") String token) {
    return authService.confirmToken(token);
  }

  @PostMapping("/change-password")
  public ResponseEntity<ApiResponseDto<ResetPasswordResponse>> changePassword(
      @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
      @Validated @RequestBody ChangePasswordRequest request) {

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new BadCredentialsException("Token không hợp lệ");
    }

    // Lấy token từ header
    String token = authorizationHeader.replace("Bearer ", "");

    // Gọi AuthService để xử lý
    ResetPasswordResponse response =
        authService.changePassword(token, request.getOldPassword(), request.getNewPassword());
    ;

    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<ResetPasswordResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  @GetMapping("/oauth2/success")
  public ResponseEntity<ApiResponseDto<SignInResponse>> loginOauth2Success(
      Authentication authentication, OAuth2AuthenticationToken authenticationToken) {
    if (authenticationToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");
    String fullName = oAuth2User.getAttribute("name");
    String provider = authenticationToken.getAuthorizedClientRegistrationId();
    boolean status = oAuth2User.getAttribute("email_verified");

    SignInResponse response = authService.signUpOAuth2(email, fullName, provider, status);

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<SignInResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<ApiResponseDto<ForgotPasswordResponse>> forgotPassword(
      @RequestBody @Valid ForgotPasswordRequest request, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin");
    }
    ForgotPasswordResponse response = authService.forgotPassword(request);

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(
            ApiResponseDto.<ForgotPasswordResponse>builder()
                .status(202)
                .message(HttpStatus.ACCEPTED.getReasonPhrase())
                .response(response)
                .build());
  }

  @PostMapping("/forgot-password/change")
  public ResponseEntity<ApiResponseDto<ResetPasswordResponse>> resetPassword(
      @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
      @Valid @RequestBody ChangeForgotPasswordRequest request,
      BindingResult bindingResult) {
    if (authorizationHeader == null) {
      throw new BadCredentialsException("Token không hợp lệ");
    }
    String token = authorizationHeader.replace("Bearer ", "");
    if (bindingResult.hasErrors()) {
      throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin");
    }

    ResetPasswordRequest newRequest = new ResetPasswordRequest();
    newRequest.setToken(token);
    newRequest.setNewPassword(request.getNewPassword());
    newRequest.setConfirmPassword(request.getConfirmPassword());

    ResetPasswordResponse response = authService.resetPassword(newRequest);

    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<ResetPasswordResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  @PostMapping("/sign-up/instructor")
  public ResponseEntity<ApiResponseDto<SignUpResponse>> signUpForInstructor(
      @RequestBody @Valid SignUpRequestForInstructor requestForInstructor) {
    SignUpResponse response = authService.signUpForInstructor(requestForInstructor);
    return ResponseEntity.ok()
        .body(
            ApiResponseDto.<SignUpResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }

  @PostMapping("/sign-in/instructor")
  public ResponseEntity<ApiResponseDto<SignInResponse>> signInForInstructor(
      @RequestBody SignInRequest request) {
    //
    SignInResponse response = authService.authenticate(request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ApiResponseDto.<SignInResponse>builder()
                .status(200)
                .message(HttpStatus.OK.getReasonPhrase())
                .response(response)
                .build());
  }
}
