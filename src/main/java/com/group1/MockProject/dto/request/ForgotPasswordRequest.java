package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ForgotPasswordRequest {
    @NotBlank(message="Email không được bỏ trống")
    @Email(message="Email không hợp lệ")
    private String email;
}
