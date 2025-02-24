package com.group1.MockProject.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentDashboardResponse {
    private int totalCourses;
    private int totalEnrollments;
    private int totalPurchasedCourses;

    //Constructor for StudentDashboardResponse
    public StudentDashboardResponse(int totalCourses, int totalEnrollments, int totalPurchasedCourses) {
        this.totalCourses = totalCourses;
        this.totalEnrollments = totalEnrollments;
        this.totalPurchasedCourses = totalPurchasedCourses;
    }
}
