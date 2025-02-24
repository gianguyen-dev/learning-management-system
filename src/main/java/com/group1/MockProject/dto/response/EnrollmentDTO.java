package com.group1.MockProject.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EnrollmentDTO {
    private int id;
    private LocalDateTime enrollmentDate;
    private int courseId;
    private String courseName;
}
