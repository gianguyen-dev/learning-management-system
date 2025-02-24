package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.ReviewRequest;
import com.group1.MockProject.dto.response.ReviewResponse;
import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Instructor;
import com.group1.MockProject.entity.Review;
import com.group1.MockProject.entity.Student;
import com.group1.MockProject.repository.CourseRepository;
import com.group1.MockProject.repository.ReviewCourseRepository;
import com.group1.MockProject.service.ReviewCourseService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ReviewCourseServiceImpl implements ReviewCourseService {

  private final ReviewCourseRepository reviewCourseRepository;
  private final CourseRepository courseRepository;

  public ReviewCourseServiceImpl(
      ReviewCourseRepository reviewCourseRepository, CourseRepository courseRepository) {
    this.reviewCourseRepository = reviewCourseRepository;
    this.courseRepository = courseRepository;
  }

  @Override
  public String addReview(ReviewRequest reviewRequest, Student student, Course course) {
    Review review = new Review();
    review.setStudent(student);
    review.setCourse(course);
    review.setCreatedAt(LocalDateTime.now());
    review.setRating(reviewRequest.getRating());
    review.setComment(reviewRequest.getComment());

    reviewCourseRepository.save(review);

    return "Đánh giá khóa học thành công.";
  }

  @Override
  public List<ReviewResponse> getAllReviews(Instructor instructor) {
    List<Course> course = courseRepository.findByInstructor(instructor);

    if (course.isEmpty()) {
      throw new EmptyResultDataAccessException("Không tìm thấy khoá học", 1);
    }

    List<ReviewResponse> allReview = new ArrayList<>();

    for (Course c : course) {
      List<Review> reviews = reviewCourseRepository.findAllReviewByCourseId(c.getId());
      allReview.addAll(
          reviews.stream()
              .map(
                  review ->
                      new ReviewResponse(
                          review.getComment(), review.getRating(), review.getCourse().getId()))
              .collect(Collectors.toList()));
    }

    return allReview;
  }
}
