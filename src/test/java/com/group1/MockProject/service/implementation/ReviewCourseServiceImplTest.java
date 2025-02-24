package com.group1.MockProject.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import com.group1.MockProject.dto.request.ReviewRequest;
import com.group1.MockProject.dto.response.ReviewResponse;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.CourseRepository;
import com.group1.MockProject.repository.ReviewCourseRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

@ExtendWith(MockitoExtension.class)
class ReviewCourseServiceImplTest {
  @Mock private ReviewCourseRepository reviewCourseRepository;

  @Mock private CourseRepository courseRepository;

  @InjectMocks private ReviewCourseServiceImpl reviewCourseService;

  private User mockUser;
  private Student mockStudent;
  private Review mockReview;
  private Course mockCourse;
  private Instructor mockInstructor;

  @BeforeEach
  void setUp() {
    mockUser = new User();
    mockUser.setId(1);
    mockUser.setEmail("mock@email.com");
    mockUser.setRole(UserRole.STUDENT);
    mockUser.setFullName("Mock User");
    mockUser.setStatus(1);

    mockStudent = new Student();
    mockStudent.setId(1);
    mockStudent.setStudentCode("520H0374");
    mockStudent.setUser(mockUser);
    mockUser.setStudent(mockStudent);

    mockCourse = new Course();
    mockCourse.setId(1);
    mockCourse.setTitle("Mock Course");
    mockCourse.setDescription("Mock Course");

    mockReview = new Review();
    mockReview.setId(1);
    mockReview.setCourse(mockCourse);
    mockReview.setStudent(mockStudent);

    User mockUserIsInstructor = new User();
    mockUserIsInstructor.setId(1);
    mockUserIsInstructor.setEmail("mockInstructor@email.com");
    mockUserIsInstructor.setPassword("password");
    mockUserIsInstructor.setFullName("Instructor");
    mockUserIsInstructor.setStatus(1);
    mockUserIsInstructor.setRole(UserRole.INSTRUCTOR);

    mockInstructor = new Instructor();
    mockInstructor.setId(1);
    mockInstructor.setUser(mockUserIsInstructor);
    mockInstructor.setName("Instructor");
    mockInstructor.setExpertise("IT");
    mockUserIsInstructor.setInstructor(mockInstructor);
  }

  @Test
  public void testAddReview_Success() {
    ReviewRequest reviewRequest = new ReviewRequest();
    reviewRequest.setComment("comment");
    reviewRequest.setRating(5);
    mockReview.setComment(reviewRequest.getComment());
    mockReview.setRating(reviewRequest.getRating());

    Mockito.when(reviewCourseRepository.save(Mockito.any(Review.class))).thenReturn(mockReview);

    String result = reviewCourseService.addReview(reviewRequest, mockStudent, mockCourse);

    Assertions.assertEquals("Đánh giá khóa học thành công.", result);
  }

  @Test
  public void testGetAllReviews_Success() {
    Mockito.when(courseRepository.findByInstructor(mockInstructor)).thenReturn(List.of(mockCourse));
    Mockito.when(reviewCourseRepository.findAllReviewByCourseId(mockCourse.getId()))
        .thenReturn(List.of(mockReview));

    List<ReviewResponse> result = reviewCourseService.getAllReviews(mockInstructor);

    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(mockReview.getComment(), result.getFirst().getComment());
    Assertions.assertEquals(mockReview.getRating(), result.getFirst().getRating());
  }

  @Test
  public void testGetAllReviews_NoCourseFound() {
    Mockito.when(courseRepository.findByInstructor(mockInstructor)).thenReturn(List.of());

    Exception exception =
        Assertions.assertThrows(
            EmptyResultDataAccessException.class,
            () -> reviewCourseService.getAllReviews(mockInstructor));

    Assertions.assertEquals("Không tìm thấy khoá học", exception.getMessage());
  }
}
