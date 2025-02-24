package com.group1.MockProject.service;

import com.group1.MockProject.dto.request.ReportCourseRequest;
import org.springframework.stereotype.Service;

@Service
public class ReportCourseService {

    public void reportCourse(ReportCourseRequest request) {
        // Ví dụ: tìm khóa học theo ID và lưu lý do báo cáo
        System.out.println("Course ID: " + request.getCourseId());
        System.out.println("Reason: " + request.getReason());
        // Thêm logic lưu vào cơ sở dữ liệu ở đây
    }
}
