package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignInRequest {
  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không hợp lệ")
  private String email;

  @NotBlank(message = "Mật khẩu không được để trống")
  @Length(min = 6, message = "Mật khẩu tối thiểu 6 kí tự")
  private String password;
}
