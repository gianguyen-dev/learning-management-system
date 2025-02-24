package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.response.InstructorResponse;
import com.group1.MockProject.dto.response.PageStudentsDTO;
import com.group1.MockProject.dto.response.StudentDTO;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.entity.Student;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.StudentRepository;
import com.group1.MockProject.repository.SubscriptionRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.service.StudentService;
import com.group1.MockProject.specification.CustomSpecification;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

  private final StudentRepository studentRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final InstructorRepository instructorRepository;
  private final UserRepository userRepository;
  private final ModelMapper mapper;

  private final Set<String> properties =
      new HashSet<>(
          Arrays.asList(
              "id",
              "studentCode",
              "email",
              "fullName",
              "address",
              "phone",
              "status",
              "createdAt",
              "updatedAt"));

  @Override
  public List<InstructorResponse> viewListSubscription(String studentEmail) {

    Student student =
        studentRepository
            .findByUserEmail(studentEmail)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy học viên", 1));
    List<Subscription> subscriptions = subscriptionRepository.findByStudent(student);
    return subscriptions.stream()
        .map(
            subscription ->
                new InstructorResponse(
                    subscription.getInstructor().getId(),
                    subscription.getInstructor().getName(),
                    subscription.getInstructor().getExpertise()))
        .collect(Collectors.toList());
  }

  @Override
  public List<InstructorResponse> searchInstructor(String name) {
    List<Instructor> instructors = instructorRepository.findByNameContainingIgnoreCase(name);
    return instructors.stream()
        .map(
            instructor ->
                new InstructorResponse(
                    instructor.getId(), instructor.getName(), instructor.getExpertise()))
        .collect(Collectors.toList());
  }

  @Override
  public Student getStudentByUserId(int userId) {
    Student student =
        (Student)
            studentRepository
                .findByUserId(userId)
                .orElseThrow(
                    () -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));
    return student;
  }

  public String subscribeToInstructor(String studentEmail, Integer instructorId) {
    User user =
        userRepository
            .findByEmail(studentEmail)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    Student student =
        studentRepository
            .findByUser(user)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy học viên", 1));

    Instructor instructor =
        instructorRepository
            .findById(instructorId)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy giảng viên", 1));

    Optional<Subscription> existingSubscription =
        subscriptionRepository.findByStudentAndInstructor(student, instructor);

    if (existingSubscription.isPresent()) {
      throw new DataIntegrityViolationException("Bạn đã đăng ký theo dõi giảng viên này rồi");
    }

    Subscription subscription = new Subscription();
    subscription.setStudent(student);
    subscription.setInstructor(instructor);
    subscription.setSubscribedAt(LocalDateTime.now());

    subscriptionRepository.save(subscription);

    return "Đăng ký theo dõi giảng viên thành công";
  }

  public String unsubscribeFromInstructor(String studentEmail, Integer instructorId) {
    User user =
        userRepository
            .findByEmail(studentEmail)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    Student student =
        studentRepository
            .findByUser(user)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy học viên", 1));

    Instructor instructor =
        instructorRepository
            .findById(instructorId)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy giảng viên", 1));

    Subscription subscription =
        subscriptionRepository
            .findByStudentAndInstructor(student, instructor)
            .orElseThrow(
                () ->
                    new DataIntegrityViolationException(
                        "Bạn chưa đăng ký theo dõi giảng viên này"));

    subscriptionRepository.delete(subscription);

    return "Hủy đăng ký theo dõi giảng viên thành công";
  }

  @Override
  public PageStudentsDTO getAllStudents(int pageNo, int pageSize, String sortBy, String sortDir) {
    // check pagination value
    if (pageNo < 0) {
      pageNo = 0;
    }
    if (pageSize < 0) {
      pageSize = 10;
    }
    // check sorting value
    if (!properties.contains(sortBy)) {
      sortBy = "id";
    }
    if (!sortDir.equalsIgnoreCase("asc") || !sortDir.equalsIgnoreCase("desc")) {
      sortDir = "asc";
    }
    // create page intance
    Pageable pageable = PageRequest.of(pageNo, pageSize);

    Page<Student> page =
        studentRepository.findAll(CustomSpecification.sortByCriteria(sortBy, sortDir), pageable);
    List<Student> students = page.getContent();
    List<StudentDTO> content =
        students.stream().map(st -> mapper.map(st.getUser(), StudentDTO.class)).toList();

    // create post response
    PageStudentsDTO response = new PageStudentsDTO();
    response.setContent(content);
    response.setPageNo(page.getNumber());
    response.setPageSize(page.getSize());
    response.setTotalElements(page.getTotalElements());
    response.setTotalPages(page.getTotalPages());
    response.setLast(page.isLast());
    return response;
  }
}
