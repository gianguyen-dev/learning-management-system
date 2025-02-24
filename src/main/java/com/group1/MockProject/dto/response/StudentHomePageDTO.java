package com.group1.MockProject.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class StudentHomePageDTO {
    // Courses, Categories, Cart, Subcription, Saved course, Notification, Enrollment
    private List<CourseDTO> courseDTOs;
    private List<CategoryDTO> categoryDTOs;
    private CartDTO cartDTOs;
    private List<SubcriptionDTO> subcriptionDTOs;
    private List<SavedCourseDTO> savedCourseDTOs;
    private List<NotificationDTO> notificationDTOs;
    private List<EnrollmentDTO> enrollmentDTOs;

}
