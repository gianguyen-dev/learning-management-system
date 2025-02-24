package com.group1.MockProject.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
