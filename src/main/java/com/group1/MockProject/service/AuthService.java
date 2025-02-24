package com.group1.MockProject.service;

import com.group1.MockProject.dto.request.ForgotPasswordRequest;
import com.group1.MockProject.dto.request.ResetPasswordRequest;
import com.group1.MockProject.dto.request.SignInRequest;
import com.group1.MockProject.dto.request.SignUpRequest;
import com.group1.MockProject.dto.request.SignUpRequestForInstructor;
import com.group1.MockProject.dto.response.*;

public interface AuthService {

  SignInResponse authenticate(SignInRequest request);

  SignUpResponse signUp(SignUpRequest request);

  SignUpResponse signUpForInstructor(SignUpRequestForInstructor requestForInstructor);

  String confirmToken(String token);

  ResetPasswordResponse changePassword(String email, String oldPassword, String newPassword);

  SignInResponse signUpOAuth2(String email, String fullName, String provider, boolean status);

  ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

  ResetPasswordResponse resetPassword(ResetPasswordRequest request);
}
