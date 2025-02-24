package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LectureRequest {
    @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
    private String name;
    @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
    private String description;
}
