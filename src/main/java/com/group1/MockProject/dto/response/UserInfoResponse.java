package com.group1.MockProject.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserInfoResponse {
    private String email;
    private String fullName;
    private String address;
    private String phone;
}
