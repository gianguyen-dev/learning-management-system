package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewCourseRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.course.Id = ?1")
    List<Review> findAllReviewByCourseId(int courseId);
}
