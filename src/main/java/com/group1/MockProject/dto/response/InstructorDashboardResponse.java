package com.group1.MockProject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class InstructorDashboardResponse {
    private long totalCourses;
    private long totalStudents; 
    private long totalSubscribers;
    private double totalRevenue;
    private double averageRating;
}