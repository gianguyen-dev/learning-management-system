package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChangeForgotPasswordRequest {
  @NotBlank(message = "Mật khẩu không được bỏ trống")
  @Length(min = 6, message = "Mật khẩu tối thiểu 6 kí tự")
  private String newPassword;

  @NotBlank(message = "Mật khẩu không được bỏ trống")
  @Length(min = 6, message = "Mật khẩu tối thiểu 6 kí tự")
  private String confirmPassword;
}
