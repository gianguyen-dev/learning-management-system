package com.group1.MockProject.service;

import com.group1.MockProject.dto.request.OTPRequest;
import com.group1.MockProject.dto.request.UpdateProfileRequest;
import com.group1.MockProject.dto.response.UpdateProfileResponse;
import com.group1.MockProject.dto.response.UserInfoResponse;


public interface UserService {

    UserInfoResponse getUserInfoByToken(String token);

    UpdateProfileResponse requestProfileUpdate(String token);

    String updateUserInfo(String token, UpdateProfileRequest request);

    void blockUserById(int id);

    void unblockUserById(int id);
}


