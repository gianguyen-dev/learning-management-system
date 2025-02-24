package com.group1.MockProject.service;

import com.group1.MockProject.dto.request.OTPRequest;

public interface OTPService {
    public String sendOTPToRequestUpdate(String email);

    boolean validateOTP(String email, String otp);

    void removeOTP(String email);
}
