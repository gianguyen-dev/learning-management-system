package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.ForgotPasswordRequest;
import com.group1.MockProject.dto.request.ResetPasswordRequest;
import com.group1.MockProject.dto.request.SignInRequest;
import com.group1.MockProject.dto.request.SignUpRequest;
import com.group1.MockProject.dto.request.SignUpRequestForInstructor;
import com.group1.MockProject.dto.response.*;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.*;
import com.group1.MockProject.service.AuthService;
import com.group1.MockProject.service.ConfirmTokenService;
import com.group1.MockProject.service.EmailService;
import com.group1.MockProject.utils.JwtUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

  private UserRepository userRepository;

  private StudentRepository studentRepository;

  private InstructorRepository instructorRepository;

  private PasswordEncoder passwordEncoder;

  private ConfirmTokenService confirmTokenService;

  private EmailService emailService;

  @Value("${APPLICATION_HOST}")
  private String HOST;

  public AuthServiceImpl(
      UserRepository userRepository,
      StudentRepository studentRepository,
      InstructorRepository instructorRepository,
      PasswordEncoder passwordEncoder,
      ConfirmTokenService confirmTokenService,
      EmailService emailService) {
    this.userRepository = userRepository;
    this.studentRepository = studentRepository;
    this.instructorRepository = instructorRepository;
    this.passwordEncoder = passwordEncoder;
    this.confirmTokenService = confirmTokenService;
    this.emailService = emailService;
  }

  public SignInResponse authenticate(SignInRequest request) {

    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Sai email hoặc mật khẩu"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("Sai email hoặc mật khẩu");
    }

    if (user.getStatus() == -1) {
      throw new AccessDeniedException("Tài khoản đã bị khóa");
    }

    // Giả sử bạn sử dụng JWT để tạo token
    String token = JwtUtil.generateToken(user); // Hàm này cần được triển khai riêng

    System.out.println("USER: " + JwtUtil.extractEmail(token));

    return new SignInResponse(token, "Bearer", "Đăng nhập thành công");
  }

  public SignUpResponse signUp(SignUpRequest request) {

    Optional<User> existedUser = userRepository.findByEmail(request.getEmail());
    if (existedUser.isPresent()) {
      throw new DataIntegrityViolationException("Người dùng đã tồn tại trong hệ thống");
    }

    if (!request.getPassword().equals(request.getConfirmPassword())) {
      throw new IllegalArgumentException("Mật khẩu không trùng khớp");
    }

    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setFullName(request.getFullName());
    user.setPhone(request.getPhone());
    user.setAddress(request.getAddress());
    user.setStatus(0);
    user.setRole(UserRole.STUDENT);

    User newUser = userRepository.save(user);

    // Create New student
    Student student = new Student();
    student.setStudentCode("STD" + LocalDateTime.now().getYear() + "" + newUser.getId());
    student.setUser(newUser);
    studentRepository.save(student);

    String token = JwtUtil.generateToken(newUser);

    // confirm trong vong 24h theo SRS
    ConfirmToken confirmToken =
        new ConfirmToken(token, LocalDateTime.now(), LocalDateTime.now().plusHours(24), newUser);

    confirmTokenService.saveConfirmToken(confirmToken);

    String link = HOST + "/api/v1/auth/sign-up/confirm?token=" + token;
    String title = "Xác nhận địa chỉ email của bạn";
    String content =
        "Để xác thực địa chỉ email đã đăng ký vui lòng nhấn <blockquote style=\"Margin: 0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">"
            + "<a href=\""
            + link
            + "\">vào đây.</a></blockquote>\n <p>Cảm ơn bạn đã đăng ký tài khoản. Hẹn gặp lại bạn! \nAdmin Group 1</p>";

    emailService.send(
        request.getEmail(), emailService.buildEmail(title, request.getEmail(), content));

    return new SignUpResponse(
        token,
        "Đăng ký thành công. Vui lòng kiểm tra email để hoàn thành xác nhận tài khoản."
            + "Nếu bạn không nhận được email. Ấn vào đây.");
  }

  public SignUpResponse signUpForInstructor(SignUpRequestForInstructor requestForInstructor) {

    Optional<User> existedUser = userRepository.findByEmail(requestForInstructor.getEmail());
    if (existedUser.isPresent()) {
      throw new EmptyResultDataAccessException("Người dùng đã tồn tại trong hệ thống", 1);
    }

    if (!requestForInstructor.getPassword().equals(requestForInstructor.getConfirmPassword())) {
      throw new IllegalArgumentException("Mật khẩu không trùng khớp");
    }

    User user = new User();
    user.setEmail(requestForInstructor.getEmail());
    user.setPassword(passwordEncoder.encode(requestForInstructor.getPassword()));
    user.setFullName(requestForInstructor.getFullName());
    user.setPhone(requestForInstructor.getPhone());
    user.setAddress(requestForInstructor.getAddress());
    user.setStatus(0);
    user.setRole(UserRole.INSTRUCTOR);

    User newUser = userRepository.save(user);

    // Create New instructor
    Instructor instructor = new Instructor();
    instructor.setExpertise(requestForInstructor.getExpertise());
    instructor.setName(requestForInstructor.getFullName());
    instructor.setUser(newUser);
    instructorRepository.save(instructor);

    String token = JwtUtil.generateToken(newUser);

    // confirm trong vong 24h theo SRS
    ConfirmToken confirmToken =
        new ConfirmToken(token, LocalDateTime.now(), LocalDateTime.now().plusHours(24), newUser);

    confirmTokenService.saveConfirmToken(confirmToken);
    //Không gửi mail
    String link = HOST + "/api/v1/auth/sign-up/confirm?token=" + token;
    String title = "Xác nhận địa chỉ email của bạn";
    String content =
            "Để xác thực địa chỉ email đã đăng ký vui lòng nhấn <blockquote style=\"Margin: 0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">"
                    + "<a href=\""
                    + link
                    + "\">vào đây.</a></blockquote>\n <p>Cảm ơn bạn đã đăng ký tài khoản. Hẹn gặp lại bạn! \nAdmin Group 1</p>";
    emailService.send(
            requestForInstructor.getEmail(), emailService.buildEmail(title, requestForInstructor.getEmail(), content));
    return new SignUpResponse(
        token, "Đăng ký thành công. Vui lòng đợi hệ thống hoàn thành xác nhận tài khoản.");
  }

  @Transactional
  public String confirmToken(String token) {
    ConfirmToken confirmationToken =
        confirmTokenService
            .getToken(token)
            .orElseThrow(() -> new BadCredentialsException("Không tìm thấy Token"));

    if (confirmationToken.getConfirmedAt() != null) {
      throw new DataIntegrityViolationException("Email đã được xác nhận");
    }

    LocalDateTime expiredAt = confirmationToken.getExpiresAt();

    if (expiredAt.isBefore(LocalDateTime.now())) {
      throw new BadCredentialsException("Token đã hết hạn");
    }

    confirmTokenService.setConfirmedAt(token);
    confirmationToken.getUser().setVerificationCode(token);
    confirmationToken.getUser().setStatus(1);
    return "Xác thực thành công, tài khoản hoạt động.";
  }

  public ResetPasswordResponse changePassword(
      String token, String oldPassword, String newPassword) {
    // Xác thực token
    if (!JwtUtil.validateToken(token)) {
      throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
    }

    // Lấy email từ token
    String email = JwtUtil.extractEmail(token);

    // Lấy thông tin người dùng
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new EmptyResultDataAccessException(
                        "Người dùng không tồn tại trong hệ thống", 1));

    // Kiểm tra mật khẩu cũ
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      throw new BadCredentialsException("Mật khẩu cũ không đúng");
    }

    // Cập nhật mật khẩu mới
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    return new ResetPasswordResponse("Đổi mật khẩu thành công");
  }

  @Override
  public SignInResponse signUpOAuth2(
      String email, String fullName, String provider, boolean status) {

    Optional<User> existedUser = userRepository.findByEmailAndProvider(email, provider);
    // IF USER NOT EXISTS
    User user = new User();
    if (existedUser.isPresent()) {
      user = existedUser.get();
      if (user.getPassword() != null) {
        String token = JwtUtil.generateToken(user);
        return new SignInResponse(token, "Bearer", "Đăng nhập với Google thành công");
      }

    } else {
      user.setEmail(email);
    }
    user.setFullName(fullName);
    user.setRole(UserRole.STUDENT);
    user.setProvider(provider);
    user.setStatus(1);

    if (user.getPassword() == null) {
      int randomPass = 100000 + (int) (Math.random() * ((999999 - 100000) + 1));
      user.setPassword(passwordEncoder.encode("" + randomPass));

      String resetToken = JwtUtil.generateToken(user);

      // Send the token to the user (e.g., via email)
      // You can implement an EmailService to handle email sending
      String link = HOST + "/api/v1/auth/forgot-password/change?token=" + resetToken;
      String title = "Đặt lại mật khẩu";
      String content =
          "Mật khẩu được tạo từ hệ thống "
              + randomPass
              + ".Vui lòng nhấn <blockquote style=\"Margin: 0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><a href=\""
              + link
              + "\">vào đây.</a> để có thể đặt lại mật khẩu của bạn.</blockquote>";

      emailService.sendDetail(
          user.getEmail(),
          emailService.buildEmail(title, user.getEmail(), content),
          "Đặt lại mật khẩu");
    }
    userRepository.save(user);
    String token = JwtUtil.generateToken(user);
    return new SignInResponse(token, "Bearer", "Đăng nhập với Google thành công");
  }

  /** Forgot password */
  public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
    // Find the user by email
    Optional<User> user = userRepository.findByEmail(request.getEmail());
    if (!user.isPresent()) {
      throw new EmptyResultDataAccessException("Người dùng không tồn tại trong hệ thống", 1);
    }
    // Generate a password reset token using JwtUtil
    String resetToken = JwtUtil.generateToken(user.get());

    // Send the token to the user (e.g., via email)
    // You can implement an EmailService to handle email sending
    String emailMessage = "Đã gửi email đổi mật khẩu";
    String link = HOST + "/api/v1/auth/forgot-password/change?token=" + resetToken;
    String title = "Khôi phục mật khẩu";

    emailService.sendDetail(
        user.get().getEmail(), buildForgotPasswordEmail(user.get().getEmail(), link), title);

    return new ForgotPasswordResponse(resetToken, emailMessage);
  }

  public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
    if (!JwtUtil.validateToken(request.getToken())) {
      throw new BadCredentialsException("Mã tái thiết lập không hợp lệ hoặc đã hết hạn");
    }

    String email = JwtUtil.extractEmail(request.getToken());
    Optional<User> user = userRepository.findByEmail(email);
    if (!user.isPresent()) {
      throw new EmptyResultDataAccessException("Người dùng không tồn tại trong hệ thống", 1);
    }
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new IllegalArgumentException("Mật khẩu không trùng khớp");
    }
    user.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user.get());

    return new ResetPasswordResponse("Đổi mật khẩu thành công");
  }

  private String buildForgotPasswordEmail(String name, String link) {
    return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
        + "\n"
        + "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n"
        + "\n"
        + "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
        + "    <tbody><tr>\n"
        + "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n"
        + "        \n"
        + "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
        + "          <tbody><tr>\n"
        + "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n"
        + "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
        + "                  <tbody><tr>\n"
        + "                    <td style=\"padding-left:10px\">\n"
        + "                  \n"
        + "                    </td>\n"
        + "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
        + "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Khôi phục mật khẩu</span>\n"
        + "                    </td>\n"
        + "                  </tr>\n"
        + "                </tbody></table>\n"
        + "              </a>\n"
        + "            </td>\n"
        + "          </tr>\n"
        + "        </tbody></table>\n"
        + "        \n"
        + "      </td>\n"
        + "    </tr>\n"
        + "  </tbody></table>\n"
        + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
        + "    <tbody><tr>\n"
        + "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n"
        + "      <td>\n"
        + "        \n"
        + "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
        + "                  <tbody><tr>\n"
        + "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n"
        + "                  </tr>\n"
        + "                </tbody></table>\n"
        + "        \n"
        + "      </td>\n"
        + "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n"
        + "    </tr>\n"
        + "  </tbody></table>\n"
        + "\n"
        + "\n"
        + "\n"
        + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
        + "    <tbody><tr>\n"
        + "      <td height=\"30\"><br></td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
        + "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
        + "        \n"
        + "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Dear "
        + name
        + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Vui lòng nhấn  <blockquote style=\"Margin: 0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><a href=\""
        + link
        + "\">vào đây</a> để khôi phục tài khoản của bạn.</blockquote>"
        + "        \n"
        + "      </td>\n"
        + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "      <td height=\"30\"><br></td>\n"
        + "    </tr>\n"
        + "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n"
        + "\n"
        + "</div></div>";
  }
}
