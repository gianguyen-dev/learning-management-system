package com.group1.MockProject.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {
    private String token;
    // Type = "Bearer"
    private String tokenType;
    private String message;
//    private String role;
}
