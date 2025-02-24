package com.group1.MockProject.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubcriptionDTO {
    private int id;
    private int instructorId; // ID của Instructor
    private String instructorName; // Tên của Instructor (nếu cần)
    private String title;
}
