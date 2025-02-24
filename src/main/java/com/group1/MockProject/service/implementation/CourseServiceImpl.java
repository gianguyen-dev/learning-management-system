package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.CourseRequest;
import com.group1.MockProject.dto.response.CategoryDTO;
import com.group1.MockProject.dto.response.CourseDTO;
import com.group1.MockProject.dto.response.InstructorDTO;
import com.group1.MockProject.dto.response.RejectCourseResponse;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.CategoryRepository;
import com.group1.MockProject.repository.CourseRepository;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.service.CourseService;
import com.group1.MockProject.utils.JwtUtil;
import com.group1.MockProject.service.EmailService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.group1.MockProject.entity.Subscription;
import com.group1.MockProject.repository.SubscriptionRepository;

@Service
public class CourseServiceImpl implements CourseService {

  private ModelMapper modelMapper;

  private CourseRepository courseRepository;

  private CategoryRepository categoryRepository;

  private InstructorRepository instructorRepository;

  private UserRepository userRepository;

  private EmailService emailService;

  private SubscriptionRepository subscriptionRepository;

  @Autowired
  public CourseServiceImpl(
      CourseRepository courseRepository,
      CategoryRepository categoryRepository,
      InstructorRepository instructorRepository,
      UserRepository userRepository,
      ModelMapper modelMapper,
      EmailService emailService,
      SubscriptionRepository subscriptionRepository) {
    this.courseRepository = courseRepository;
    this.categoryRepository = categoryRepository;
    this.instructorRepository = instructorRepository;
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
    this.emailService = emailService;
    this.subscriptionRepository = subscriptionRepository;
  }

  private CategoryDTO maptoDTO(Category category) {
    return modelMapper.map(category, CategoryDTO.class);
  }

  private InstructorDTO maptoDTOIns(Instructor instructor) {
    return modelMapper.map(instructor, InstructorDTO.class);
  }

  @Override
  public CourseDTO createCourse(
      com.group1.MockProject.dto.request.CourseRequest courseRequest, String token) {

    String email = JwtUtil.extractEmail(token);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));
    ;
    if (user.getStatus()==1){
      // Convert the CreateCourseRequest to a Course entity
      Course course = new Course();
      course.setTitle(courseRequest.getTitle());
      course.setDescription(courseRequest.getDescription());
      course.setPrice(courseRequest.getPrice());
      course.setStatus(0);
      course.setCreatedAt(LocalDateTime.now());
      // Fetch Instructor from DB using categoryid
      Category category =
              categoryRepository
                      .findById(courseRequest.getCategoryId())
                      .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy danh mục", 1));

      // Set Category and Instructor
      course.setCategory(category);
      course.setInstructor(user.getInstructor());

      // Save the course entity in the database
      course = courseRepository.save(course);


      CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
      InstructorDTO instructorDTO = modelMapper.map(user,InstructorDTO.class);
      instructorDTO.setExpertise(user.getInstructor().getExpertise());
      instructorDTO.setId(user.getInstructor().getId());
      courseDTO.setInstructor(instructorDTO);
      return courseDTO;
    }
    else {
      throw new IllegalArgumentException("Bạn không có quyền để thêm khóa học");
    }
  }

  @Override
  public CourseDTO updateCourse(int courseId, CourseRequest courseRequest, String token) {
    // Find the existing course by ID
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khoá học", 1));
    //        int instructorId = JwtUtil.extractUserIdFromToken(token);
    String email = JwtUtil.extractEmail(token);

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    if (course.getInstructor().getUser().getId() != user.getId()) {
      throw new AccessDeniedException("Bạn không có quyền để cập nhật khoá học này");
    }

    // Update course fields with the values from the request
    course.setTitle(courseRequest.getTitle());
    course.setDescription(courseRequest.getDescription());
    course.setPrice(courseRequest.getPrice());
    course.setUpdatedAt(LocalDateTime.now());

    // Fetch and update Category and Instructor if IDs are provided in the request
    Category category =
        categoryRepository
            .findById(courseRequest.getCategoryId())
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy danh mục", 1));
    course.setCategory(category);

    // Save the updated course
    course = courseRepository.save(course);
    // Return the updated CourseDTO
    CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
    InstructorDTO instructorDTO = modelMapper.map(user,InstructorDTO.class);
    instructorDTO.setExpertise(user.getInstructor().getExpertise());
    instructorDTO.setId(user.getInstructor().getId());
    courseDTO.setInstructor(instructorDTO);
    return courseDTO;
  }

  @Override
  public List<CourseDTO> getCoursesByInstructor(String token) {

    String email = JwtUtil.extractEmail(token);

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    //     Kiểm tra quyền sở hữu
    if (user.getInstructor() == null) {
      throw new AccessDeniedException("Bạn không có quyền xem khoá học này");
    }
    List<Course> courses = courseRepository.findByInstructor(user.getInstructor());

    CourseDTO courseDTO = modelMapper.map(courses, CourseDTO.class);
    InstructorDTO instructorDTO = modelMapper.map(user,InstructorDTO.class);
    instructorDTO.setExpertise(user.getInstructor().getExpertise());
    instructorDTO.setId(user.getInstructor().getId());
    courseDTO.setInstructor(instructorDTO);

    // Chuyển đổi từ Course entity sang CourseDTO
    return courses.stream()
        .map(
            course ->
                new CourseDTO(
                    course.getId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getPrice(),
                    courseDTO.getInstructor(),
                    maptoDTO(course.getCategory())))
        .collect(Collectors.toList());
  }

  @Override
  public CourseDTO getCourseById(int id) {

    // Lấy danh sách khóa học từ repository
    Course course =
        courseRepository
            .findById(id)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));

    // Chuyển đổi từ Course entity sang CourseDTO
    return modelMapper.map(course, CourseDTO.class);
  }

  @Override
  public void deleteCourse(int courseId, String token) {

    String email = JwtUtil.extractEmail(token);

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khoá học", 1));

    // Kiểm tra quyền sở hữu
    if (course.getInstructor().getUser().getId() != user.getId()) {
      throw new AccessDeniedException("Bạn không có quyền để xoá khoá học này");
    }

    if (!course.getEnrollment().isEmpty()) {
      throw new DataIntegrityViolationException(
          "Không thể xoá khoá học khi còn học viên đang tham gia");
    }

    courseRepository.delete(course);
  }

  @Override
  public void updateCourseStatus(int courseId, CourseStatus status, String reason) {
    Course course = courseRepository
        .findById(courseId)
        .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khoá học", 1));

    course.setStatus(status.getValue());

    if (status == CourseStatus.REJECTED) {
        course.setRejectionReason(reason);
        // Gửi email thông báo reject cho instructor
        String instructorEmail = course.getInstructor().getUser().getEmail();
        String instructorName = course.getInstructor().getUser().getFullName();
        emailService.sendNotification(
            instructorEmail,
            emailService.buildRejectCourseEmail(course.getTitle(), instructorName, reason)
        );
    } else if (status == CourseStatus.APPROVED) {
        String instructorName = course.getInstructor().getUser().getFullName();
        String courseTitle = course.getTitle();

        // 1. Gửi email cho instructor
        String instructorEmail = course.getInstructor().getUser().getEmail();
        emailService.sendNotification(
            instructorEmail,
            emailService.buildApprovedCourseEmailForInstructor(courseTitle, instructorName)
        );

        // 2. Gửi email cho tất cả subscriber
        List<Subscription> subscribers = subscriptionRepository
            .findByInstructor(course.getInstructor());

        for (Subscription sub : subscribers) {
            User student = sub.getStudent().getUser();
            emailService.sendNotification(
                student.getEmail(),
                emailService.buildApprovedCourseEmailForStudent(
                    courseTitle,
                    instructorName,
                    student.getFullName()
                )
            );
        }
    }

    courseRepository.save(course);
  }

  @Override
  public List<RejectCourseResponse> viewAllRejectedCoursesByInstructor(int instructorId) {
    List<Course> courseResponses = courseRepository.findByInstructorId(instructorId);
    List<RejectCourseResponse> rejectCourseResponses =
        courseResponses.stream()
            .filter(course -> course.getStatus() == 2)
            .map(
                course -> {
                  RejectCourseResponse rejectCourseResponse = new RejectCourseResponse();
                  rejectCourseResponse.setId(course.getId());
                  rejectCourseResponse.setTitle(course.getTitle());
                  rejectCourseResponse.setDescription(course.getDescription());
                  rejectCourseResponse.setPrice(course.getPrice());
                  rejectCourseResponse.setCategory(maptoDTO(course.getCategory()));
                  rejectCourseResponse.setRejectReason(course.getRejectionReason());

                  return rejectCourseResponse;
                })
            .toList();

    return rejectCourseResponses;
  }

  @Override
  public CourseDTO reSubmitCourse(CourseRequest courseRequest, String token, int courseId) {
    String email = JwtUtil.extractEmail(token);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khoá học", 1));

    if (course.getInstructor().getUser().getId() != user.getId()) {
      throw new AccessDeniedException("Bạn không có quyền để cập nhật khoá học này");
    }

    course.setTitle(courseRequest.getTitle());
    course.setDescription(courseRequest.getDescription());
    course.setPrice(courseRequest.getPrice());
    course.setRejectionReason(null);
    course.setStatus(CourseStatus.PENDING.getValue());

    // Fetch and update Category and Instructor if IDs are provided in the request
    Category category =
        categoryRepository
            .findById(courseRequest.getCategoryId())
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy danh mục", 1));

    course.setCategory(category);

    // Save the updated course
    course = courseRepository.save(course);

    // Return the updated CourseDTO
    return modelMapper.map(course, CourseDTO.class);
  }

  @Override
  public void updateCourseStatus(int courseId, CourseStatus status) {
    updateCourseStatus(courseId, status, null);
  }

  @Override
  public List<CourseDTO> getAllCourses() {
    // Logic để lấy danh sách khóa học từ repository
    // Ví dụ: return courseRepository.findAll().stream().map(...).collect(Collectors.toList());
    return null; // Thay thế bằng logic thực tế
  }
  //    @Override
  //    public List<CourseDTO> getAllCourses() {
  //        // Get all course
  //        List<Course> courses = courseRepository.findAll();
  //
  //        // Get all category
  //        List<Category> categories = categoryRepository.findAll();
  //        // Map category entity to dto
  //        List<CategoryDTO> categoryDTOList = categories.stream().map(category ->
  // maptoDTO(category)).collect(Collectors.toList());
  //        // Map course entity to dto
  //        List<CourseDTO> response = courses.stream().map(course ->
  // mapToDTO(course)).collect(Collectors.toList());
  //        return response;
  //    }
  @Override
  public List<CourseDTO> studentGetAllCourses(String token) {
    String email = JwtUtil.extractEmail(token);

    User user =
            userRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

//     Kiểm tra quyền sở hữu
    if (user.getStatus() == 0) {
      throw new AccessDeniedException("Bạn phải xác nhận email mới được xem các khoá học này");
    }
    // Kiểm tra vai trò người dùng
    if (!user.getRole().toString().equals("STUDENT")) {
      throw new AccessDeniedException("Bạn không có quyền XEM khoá học");
    }
    // Lấy danh sách khóa học từ repository
    List<Course> courses = courseRepository.findAll();

    // Chuyển đổi từ Course entity sang CourseDTO
    return courses.stream()
            .map(course -> new CourseDTO(
                    course.getId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getPrice(),
                    modelMapper.map(course.getInstructor(), InstructorDTO.class),
                    maptoDTO(course.getCategory())))
            .collect(Collectors.toList());
  }
}
