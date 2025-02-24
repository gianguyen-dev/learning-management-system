package com.group1.MockProject.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RejectCourseResponse {
  private int id;
  private String title;
  private String description;
  private Double price;
  private CategoryDTO category;
  private String rejectReason;
}
