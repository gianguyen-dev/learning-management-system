package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SectionRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    @NotBlank(message = "Mô tả không được để trống")
    private String description;
}
