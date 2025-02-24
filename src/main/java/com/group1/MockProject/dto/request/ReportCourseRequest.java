package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReportCourseRequest {
    @NotBlank(message = "Course ID is required")
    private int courseId;

    @NotBlank(message = "Reason is required")
    private String reason;
}
