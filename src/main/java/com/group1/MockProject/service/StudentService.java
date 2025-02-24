package com.group1.MockProject.service;

import com.group1.MockProject.dto.response.InstructorResponse;
import com.group1.MockProject.dto.response.PageStudentsDTO;
import com.group1.MockProject.entity.Student;
import java.util.List;

public interface StudentService {
  List<InstructorResponse> viewListSubscription(String studentEmail);

  List<InstructorResponse> searchInstructor(String name);

  public Student getStudentByUserId(int userId);

  String subscribeToInstructor(String email, Integer instructorId);

  String unsubscribeFromInstructor(String email, Integer instructorId);

  //    List<>
  PageStudentsDTO getAllStudents(int pageNo, int pageSize, String sortBy, String sortDir);
}
