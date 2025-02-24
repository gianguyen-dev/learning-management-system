package com.group1.MockProject.service;

import com.group1.MockProject.dto.response.CourseStudentAnalyticsResponse;
import org.springframework.stereotype.Service;

@Service
public class CourseStudentAnalyticsService {

    public CourseStudentAnalyticsResponse getCourseStudentData() {
        // Logic để lấy dữ liệu phân tích khóa học/sinh viên
        return new CourseStudentAnalyticsResponse(10, 50, 200); // Ví dụ dữ liệu
    }
}