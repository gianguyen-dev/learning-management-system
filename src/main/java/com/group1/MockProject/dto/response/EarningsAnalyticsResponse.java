package com.group1.MockProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EarningsAnalyticsResponse {
  private Double totalEarnings;
  private int totalCourses;
  private int totalStudents;
}
