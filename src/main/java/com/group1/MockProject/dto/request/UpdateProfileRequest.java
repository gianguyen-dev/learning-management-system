package com.group1.MockProject.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateProfileRequest {
    @Valid
    private ProfileRequest request;

    @NotBlank(message = "Mã xác thực không được để trống")
    private String otp;
}