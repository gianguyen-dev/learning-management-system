package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestForInstructor {
  @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
  private String email;

  @Length(min = 6, message = "Mật khẩu tối thiểu 6 kí tự")
  @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
  private String password;

  @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
  private String confirmPassword;

  @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
  private String fullName;

  @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
  private String address;

  @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
  @Min(value = 10, message = "Vui lòng điền đầy đủ thông tin")
  private String phone;

  @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
  private String expertise;
}
