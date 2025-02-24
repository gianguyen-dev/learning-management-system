package com.group1.MockProject.dto.response;

import com.group1.MockProject.entity.SavedCourse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetSavedCourseResponse {
    private List<SavedCourse> savedCourse;
    private String message;
}
