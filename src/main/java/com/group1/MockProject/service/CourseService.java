package com.group1.MockProject.service;

import com.group1.MockProject.dto.request.CourseRequest;
import com.group1.MockProject.dto.response.CourseDTO;
import com.group1.MockProject.dto.response.RejectCourseResponse;
import com.group1.MockProject.entity.CourseStatus;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface CourseService {
  CourseDTO createCourse(
      com.group1.MockProject.dto.request.CourseRequest courseRequest, String token);

  CourseDTO updateCourse(
      int courseId, com.group1.MockProject.dto.request.CourseRequest courseRequest, String token);
  List<CourseDTO> getCoursesByInstructor(String token);
  CourseDTO getCourseById(int id);
  void deleteCourse(int courseId, String token);

  void updateCourseStatus(int courseId, CourseStatus status);

  void updateCourseStatus(int courseId, CourseStatus status, String reason);

  List<RejectCourseResponse> viewAllRejectedCoursesByInstructor(int instructorId);

  CourseDTO reSubmitCourse(CourseRequest course, String token, int courseId);

  List<CourseDTO> getAllCourses();
  List<CourseDTO> studentGetAllCourses(String token);
}
