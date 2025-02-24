package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
  @NotBlank(message = "Mật khẩu cũ không được để trống")
  @Length(min = 6, message = "Mật khẩu tối thiểu 6 kí tự")
  private String oldPassword;

  @NotBlank(message = "Mật khẩu mới không được để trống")
  @Length(min = 6, message = "Mật khẩu tối thiểu 6 kí tự")
  private String newPassword;
}
