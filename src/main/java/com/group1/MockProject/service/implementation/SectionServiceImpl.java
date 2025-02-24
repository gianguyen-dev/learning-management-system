package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.SectionRequest;
import com.group1.MockProject.dto.response.SectionResponse;
import com.group1.MockProject.dto.response.SectionResponseForStudent;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.*;
import com.group1.MockProject.service.SectionService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SectionServiceImpl implements SectionService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private ModelMapper mapper;


    public SectionServiceImpl(CourseRepository courseRepository,
            SectionRepository sectionRepository,
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
                              ModelMapper mapper) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.mapper = mapper;
    }
    @Override
    public List<SectionResponse> getAllSection(int courseId, String email) {
        if (!checkApprovedInstructor(email)) {
            throw new AccessDeniedException("Giảng viên chưa được duyệt");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));
        if (checkCourseBelongToInstructor(email, course)) {
            List<Section> sections = course.getSections();
            List<SectionResponse> response = new ArrayList<>();
            if (!sections.isEmpty()) {
                response = sections.stream()
                        .map(s -> mapper.map(s, SectionResponse.class))
                        .toList();
            }
            return response;
        } else {
            throw new AccessDeniedException("Bạn không có quyền truy cập");
        }

    }

    @Override
    public SectionResponse getSectionById(int courseId, long sectionId, String email) {
        if (!checkApprovedInstructor(email)) {
            throw new AccessDeniedException("Giảng viên chưa được duyệt");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));
        if (checkCourseBelongToInstructor(email, course)) {
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy chương", 1));
            return mapper.map(section, SectionResponse.class);
        } else
            throw new AccessDeniedException("Bạn không có quyền truy cập");
    }

    @Override
    public SectionResponse createSection(int courseId, SectionRequest sectionRequest, String email) {
        if (!checkApprovedInstructor(email)) {
            throw new AccessDeniedException("Giảng viên chưa được duyệt");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));
        if (checkCourseBelongToInstructor(email, course)) {
            if (course.getStatus() != 1) {
                throw new IllegalArgumentException("Khóa học chưa được duyệt");
            }
            Section section = mapper.map(sectionRequest, Section.class);
            section.setCourse(course);
            Section newSection = sectionRepository.save(section);
            SectionResponse response = mapper.map(newSection, SectionResponse.class);
            return response;
        } else
            throw new AccessDeniedException("Bạn không có quyền truy cập");

    }

    @Override
    public SectionResponse updateSection(int courseId, long sectionId, SectionRequest sectionRequest, String email) {
        if (!checkApprovedInstructor(email)) {
            throw new AccessDeniedException("Giảng viên chưa được duyệt");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));
        if (checkCourseBelongToInstructor(email,course)) {
            if (course.getStatus() != 1) {
                throw new IllegalArgumentException("Khóa học chưa được duyệt");
            }
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy chương", 1));
            if (section.getCourse().getId() != course.getId()) {
                throw new EmptyResultDataAccessException("Chương không thuộc về khóa học này", 1);
            }
            section.setTitle(sectionRequest.getTitle());
            section.setDescription(sectionRequest.getDescription());
            Section updatedSection = sectionRepository.save(section);
            SectionResponse response = mapper.map(updatedSection, SectionResponse.class);
            return response;
        } else
            throw new AccessDeniedException("Bạn không có quyền truy cập");
    }

    @Override
    public void deleteSectionById(int courseId, long sectionId, String email) {
        if (!checkApprovedInstructor(email)) {
            throw new AccessDeniedException("Giảng viên chưa được duyệt");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));
        if (checkCourseBelongToInstructor(email, course)) {
            if (course.getStatus() != 1) {
                throw new IllegalArgumentException("Khóa học chưa được duyệt");
            }
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy chương", 1));
            if (section.getCourse().getId() != course.getId()) {
                throw new EmptyResultDataAccessException("Chương không thuộc về khóa học này", 1);
            }
            sectionRepository.delete(section);
        } else
            throw new AccessDeniedException("Bạn không có quyền truy cập");
    }

    @Override
    public <T> List<T> getAllSectionForStudent(int courseId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        "Người dùng không tồn tại trong hệ thống", 1));
        Student student = user.getStudent();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));

        Optional<Enrollment> optional = enrollmentRepository.findByCourse(course);
        if (optional.isPresent()) {
            Enrollment enrollment = optional.get();
            if (enrollment.getStudent().getId() == student.getId()) {
                List<Section> sections = course.getSections();
                List<SectionResponse> response = new ArrayList<>();
                if (!sections.isEmpty()) {
                    response = sections.stream()
                            .map(s -> mapper.map(s, SectionResponse.class))
                            .toList();
                }
                return (List<T>) response;
            }
        }
        List<Section> sections = course.getSections();
        List<SectionResponseForStudent> response = new ArrayList<>();
        if (!sections.isEmpty()) {
            response = sections.stream()
                    .map(s -> mapper.map(s, SectionResponseForStudent.class))
                    .toList();
        }
        return (List<T>) response;
    }

    @Override
    public <T> T getSectionByIdForStudent(int courseId, long sectionId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        "Người dùng không tồn tại trong hệ thống", 1));
        Student student = user.getStudent();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy chương", 1));
        Optional<Enrollment> optional = enrollmentRepository.findByCourse(course);
        if (optional.isPresent()) {
            Enrollment enrollment = optional.get();
            if (enrollment.getStudent().getId() == student.getId()) {
                return (T) mapper.map(section,SectionResponse.class);
            }
        }

        return (T) mapper.map(section, SectionResponseForStudent.class);
    }

    private boolean checkCourseBelongToInstructor(String email, Course course) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        "Người dùng không tồn tại trong hệ thống", 1));
        Instructor instructor = user.getInstructor();
        return course.getInstructor().getId() == instructor.getId();
    }

    private boolean checkApprovedInstructor(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        "Người dùng không tồn tại trong hệ thống", 1));
        Instructor instructor = user.getInstructor();
        if(instructor.getUser().getId() == user.getId()) {
            if(user.getStatus() == 1) {
                return true;
            }
        }
        return false;
    }
}
