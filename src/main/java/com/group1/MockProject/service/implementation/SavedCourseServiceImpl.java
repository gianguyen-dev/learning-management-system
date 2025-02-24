package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.*;
import com.group1.MockProject.dto.response.GetSavedCourseResponse;
import com.group1.MockProject.entity.SavedCourse;
import com.group1.MockProject.entity.Student;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.repository.SavedCourseRepository;
import com.group1.MockProject.repository.StudentRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.service.SavedCourseService;
import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SavedCourseServiceImpl implements SavedCourseService {
  private final StudentRepository studentRepository;
  private final SavedCourseRepository savedCourseRepository;
  private final UserRepository userRepository;

  @Override
  public GetSavedCourseResponse getSavedCoursesByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new EmptyResultDataAccessException("Không tìm thấy người dùng", 1);
    }
    Student student = user.get().getStudent();
    if (student == null) {
      throw new AccessDeniedException("Bạn không có quyền đăng kí khóa học");
    }

    List<SavedCourse> savedCourses = savedCourseRepository.findByStudent(student);

    if (savedCourses.isEmpty()) {
      throw new EmptyResultDataAccessException("Không tìm thấy khóa học đã lưu", 1);
    }
    return new GetSavedCourseResponse(savedCourses, "Lấy danh sách khóa học thành công");
  }

}
