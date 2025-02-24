package com.group1.MockProject.service;

import com.group1.MockProject.dto.response.*;
import com.group1.MockProject.entity.SavedCourse;

public interface SavedCourseService {
  GetSavedCourseResponse getSavedCoursesByEmail(String email);

//  SavedCourse createSavedCourses(String email, Integer id);
}
