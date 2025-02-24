package com.group1.MockProject.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InstructorDTO {
    private int id;
    private String expertise;
    private String email;
    private String fullName;
    private String address;
    private String phone;
    private String provider;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
