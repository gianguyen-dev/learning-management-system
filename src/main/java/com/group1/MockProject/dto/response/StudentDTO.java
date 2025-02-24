package com.group1.MockProject.dto.response;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentDTO {
    private int id;
    private String studentCode;
    private String email;
    private String fullName;
    private String address;
    private String phone;
    private String provider;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
