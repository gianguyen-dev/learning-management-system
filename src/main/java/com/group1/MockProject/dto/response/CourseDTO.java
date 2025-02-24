package com.group1.MockProject.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.group1.MockProject.entity.Instructor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDTO {

  private int id;
  private String title;
  private String description;
  private Double price;
  private InstructorDTO instructor;
  private CategoryDTO category;
}
