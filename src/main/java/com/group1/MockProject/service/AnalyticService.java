package com.group1.MockProject.service;

import com.group1.MockProject.dto.AnalyticDTO;

public interface AnalyticService {
    AnalyticDTO.AnalyticResponse getInstructorAnalytic(String email);
}
