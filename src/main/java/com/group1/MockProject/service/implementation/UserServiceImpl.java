package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.OTPRequest;
import com.group1.MockProject.dto.request.ProfileRequest;
import com.group1.MockProject.dto.request.UpdateProfileRequest;
import com.group1.MockProject.dto.response.UpdateProfileResponse;
import com.group1.MockProject.dto.response.UserInfoResponse;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.service.EmailService;
import com.group1.MockProject.service.OTPService;
import com.group1.MockProject.service.UserService;
import com.group1.MockProject.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final ModelMapper mapper;
  private final OTPService otpService;

  @Value("${APPLICATION_HOST}")
  private String HOST;

  public UserServiceImpl(
          UserRepository userRepository, ModelMapper mapper,
          OTPService otpService) {
    this.userRepository = userRepository;
    this.mapper = mapper;
    this.otpService = otpService;
  }

  @Override
  public UserInfoResponse getUserInfoByToken(String token) {

    String email = JwtUtil.extractEmail(token);

    Optional<User> existedUser = userRepository.findByEmail(email);
    if (existedUser.isEmpty()) {
      throw new EmptyResultDataAccessException("Người dùng không tồn tại", 1);
    }

    return mapper.map(existedUser, UserInfoResponse.class);
  }

  @Override
  public UpdateProfileResponse requestProfileUpdate(String token) {
    String email = JwtUtil.extractEmail(token);

    User user =
            userRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));


    String message = otpService.sendOTPToRequestUpdate(email);

    return new UpdateProfileResponse(message);
  }

  @Override
  public String updateUserInfo(String token, UpdateProfileRequest request) {

    String email = JwtUtil.extractEmail(token);

    User user =
            userRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));
    if(otpService.validateOTP(email, request.getOtp())) {
      ProfileRequest profileRequest = request.getRequest();
      user.setFullName(profileRequest.getFullName());
      user.setPhone(profileRequest.getPhone());
      user.setAddress(profileRequest.getAddress());
      userRepository.save(user);
      otpService.removeOTP(user.getEmail());
      return "Cập nhật thông tin cá nhân thành công";
    }

    return "Mã xác thực không chính xác";
  }

  @Override
  public void blockUserById(int id) {
    User user =
            userRepository
                    .findById(id)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));
    user.setStatus(-1);
    userRepository.save(user);
  }

  @Override
  public void unblockUserById(int id) {
    User user =
            userRepository
                    .findById(id)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));
    user.setStatus(1);
    userRepository.save(user);
  }
}
