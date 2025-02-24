package com.group1.MockProject.service;

import com.group1.MockProject.dto.request.ReviewRequest;
import com.group1.MockProject.dto.response.ReviewResponse;
import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Instructor;
import com.group1.MockProject.entity.Student;

import java.util.List;

public interface ReviewCourseService {
    String addReview(ReviewRequest reviewRequest, Student student, Course course);
    List<ReviewResponse> getAllReviews(Instructor instructor);
}
