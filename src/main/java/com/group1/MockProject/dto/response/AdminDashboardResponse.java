package com.group1.MockProject.dto.response;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AdminDashboardResponse {
  // Thống kê người dùng
  private int totalUsers;
  private int totalStudents;
  private int totalInstructors;
  private int newUsersThisMonth;

  // Thống kê khóa học
  private int totalCourses;
  private int pendingCourses;
  private int approvedCourses;
  private int rejectedCourses;

  // Thống kê doanh thu
  private Double totalRevenue;
  private Double revenueThisMonth;
  private Double revenueLastMonth;

  // Thống kê đánh giá
  private Double averageRating;
  private int totalReviews;
}
