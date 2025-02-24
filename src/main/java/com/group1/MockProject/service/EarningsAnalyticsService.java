package com.group1.MockProject.service;

import com.group1.MockProject.dto.response.EarningsAnalyticsResponse;
import org.springframework.stereotype.Service;

@Service
public class EarningsAnalyticsService {

    public EarningsAnalyticsResponse getEarningsData() {
        // Logic để lấy dữ liệu phân tích thu nhập
        return new EarningsAnalyticsResponse(1000.0, 10, 50); // Ví dụ dữ liệu
    }
}