package com.group1.MockProject.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GuestHomePageDTO {
    private List<CourseDTO> course;
    private List<CategoryDTO> categories;
}
