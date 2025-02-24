package com.group1.MockProject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReSubmitCourseRequest {
  private int courseId;
  private String updatedContent; // Nội dung đã được cập nhật
}
