package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.OTPRequest;
import com.group1.MockProject.entity.OTP;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.repository.OTPRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.service.EmailService;
import com.group1.MockProject.service.OTPService;
import com.group1.MockProject.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPServiceImpl implements OTPService {
    private final OTPRepository otpRepository;

    private final EmailService emailService;

    private final UserRepository userRepository;

    public OTPServiceImpl(OTPRepository otpRepository,
                          EmailService emailService,
                          UserRepository userRepository) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Override
    public String sendOTPToRequestUpdate(String email) {

        // Generate OTP
        String otpCode = JwtUtil.generateOTP();

        Optional<OTP> existedOtp = otpRepository.findByEmail(email);
        if (existedOtp.isEmpty()) {
            OTP otp = OTP.builder()
                    .email(email)
                    .otp(otpCode)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(2)) // Expired after 2 minutes
                    .build();
            otpRepository.save(otp);
        } else if (existedOtp.isPresent()) {
            OTP otp = existedOtp.get();
            otp.setOtp(otpCode);
            otp.setCreatedAt(LocalDateTime.now());
            otp.setExpiresAt(LocalDateTime.now().plusMinutes(2));
            otpRepository.save(otp);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại trong hệ thống"));

        String title = "Cập nhật thông tin cá nhân";
        String content =
                "Mã xác minh của bạn <blockquote style=\"margin: 0 0 20px; border-left: 5px solid #4CAF50; padding: 10px 15px; font-size: 16px; line-height: 24px; background-color: #f9f9f9; color: #333;\">"
                        + "<p><strong>" + otpCode + "</strong></p><br>"
                        + "<p>Hết hạn sau 2 phút</p>"
                        + "</blockquote>";


//        emailService.buildEmail(title,user.getFullName(),content);
        emailService.sendDetail(
                email,
                emailService.buildEmail(title,user.getFullName(),content),
                title);

        return "Đã gửi mã xác minh, kiểm tra email của bạn";
    }

    @Override
    public boolean validateOTP(String email, String otp) {

        OTP existOTP = otpRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Mã xác minh không tồn tại"));

        if(existOTP.getExpiresAt().isBefore(LocalDateTime.now())){
            throw  new IllegalArgumentException("Mã xác minh đã hết hạn");
        }

        if (existOTP.getOtp()!=null && existOTP.getOtp().equals(otp)){
            return true;
        }

        return false;
    }

    @Override
    public void removeOTP(String email) {
        OTP existOTP = otpRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Mã xác minh không tồn tại"));
        existOTP.setOtp(null);
        otpRepository.save(existOTP);
    }
}
