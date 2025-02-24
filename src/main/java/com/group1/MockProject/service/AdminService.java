package com.group1.MockProject.service;

import com.group1.MockProject.dto.request.SignInRequest;
import com.group1.MockProject.dto.response.InstructorResponse;
import com.group1.MockProject.dto.response.SignInResponse;
import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.CourseStatus;
import com.group1.MockProject.entity.User;
import org.springframework.stereotype.Service;
import com.group1.MockProject.dto.response.AdminDashboardResponse;
import com.group1.MockProject.dto.response.CourseDTO;

import java.util.List;

public interface AdminService {
    AdminDashboardResponse getDashboardData();
    List<User> getAllUsers();
    User setBlockUser(int userId);
    User setApprovedUser(int userId);
    SignInResponse authenticate(SignInRequest request);
    void deleteUser(int userId);
}
