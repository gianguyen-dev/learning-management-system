package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Instructor;
import com.group1.MockProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer>, JpaSpecificationExecutor<Instructor> {
    Optional<Instructor> findById(Integer id);
    List<Instructor> findByNameContainingIgnoreCase(String name);
    Optional<Instructor> findInstructorByUser(User user);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId")
    long countCoursesByInstructor(@Param("instructorId") int instructorId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.instructor.id = :instructorId")
    long countEnrollmentsByInstructor(@Param("instructorId") int instructorId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.instructor.id = :instructorId")
    long countSubscribersByInstructor(@Param("instructorId") int instructorId);

    @Query("SELECT SUM(p.total_price) FROM Payment p WHERE p.student.id IN " +
            "(SELECT e.student.id FROM Enrollment e WHERE e.course.instructor.id = :instructorId)")
    Double calculateTotalRevenue(@Param("instructorId") int instructorId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.instructor.id = :instructorId")
    Double calculateAverageRating(@Param("instructorId") int instructorId);


//    @Query("SELECT i FROM Instructor i JOIN i.user u ORDER BY u.status")
//    Page<Instructor> findAllWithSortByUserStatus(Pageable pageable);

}
