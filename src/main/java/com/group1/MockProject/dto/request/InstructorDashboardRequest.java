package com.group1.MockProject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstructorDashboardRequest {
    private int totalCourses;
    private int totalStudents;
    private int totalEnrollments;
    private int totalPurchasedCourses;
}
