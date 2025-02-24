package com.group1.MockProject.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SavedCourseDTO {
    private int id;
    private int courseId; // ID của Course
    private String courseName; // Tên của Course (nếu cần)
    private String description;
}
