package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
  @NotBlank(message = "Tiêu đề không được để trống")
  private String title;

  @NotBlank(message = "Mô tả không được để trống")
  private String description;

  @NotNull(message = "Giá không được để trống")
  private Double price;

  private int instructorId;

  // Assuming the instructor is selected by ID
  @NotBlank(message = "Bạn phải phân loại khóa học")
  private int categoryId; // Assuming the category is selected by ID
}
