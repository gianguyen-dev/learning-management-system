package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    @Query("SELECT l FROM Lecture l WHERE l.section.course.id = :courseId AND l.section.id = :sectionId")
    List<Lecture> findByCourseIdAndSectionId(@Param("courseId") int courseId, @Param("sectionId") long sectionId);
}
