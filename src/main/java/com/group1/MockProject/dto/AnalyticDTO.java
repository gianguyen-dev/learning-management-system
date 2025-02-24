package com.group1.MockProject.dto;

import com.group1.MockProject.entity.Analytic;
import lombok.Builder;

public abstract class AnalyticDTO {
    @Builder
    public static class AnalyticResponse {
        public Analytic analytic;
    }
}