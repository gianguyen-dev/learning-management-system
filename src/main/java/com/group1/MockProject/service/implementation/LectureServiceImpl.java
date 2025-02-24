package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.LectureRequest;
import com.group1.MockProject.dto.request.SectionRequest;
import com.group1.MockProject.dto.response.LectureResponse;
import com.group1.MockProject.dto.response.SectionResponse;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.*;
import com.group1.MockProject.service.FileService;
import com.group1.MockProject.service.LectureService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LectureServiceImpl implements LectureService {
    private final LectureRepository lectureRepository;
    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final FileService fileService;
    private ModelMapper mapper;

    public LectureServiceImpl(LectureRepository lectureRepository,
                              SectionRepository sectionRepository,
                              CourseRepository courseRepository,
                              UserRepository userRepository,
                              EnrollmentRepository enrollmentRepository,
                              ModelMapper mapper,
                              FileService fileService) {
        this.lectureRepository = lectureRepository;
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.fileService = fileService;
        this.mapper = mapper;
    }

    @Override
    public List<LectureResponse> getAllLecturesBelongToSection(int courseId, long sectionId, String email) {
        if (!checkApprovedInstructor(email)) {
            throw new AccessDeniedException("Giảng viên chưa được duyệt");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));
        if (checkCourseBelongToInstructor(email, course)) {
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy chương", 1));
            List<Lecture> lectures =  lectureRepository.findByCourseIdAndSectionId(course.getId(), section.getId());
            List<LectureResponse> response = new ArrayList<>();
            if(!lectures.isEmpty()) {
                response = lectures.stream()
                        .map(l -> mapper.map(l,LectureResponse.class))
                        .toList();
            }
            return response;
        } else
            throw new AccessDeniedException("Bạn không có quyền truy cập");
    }

    @Override
    public LectureResponse getLectureById(int courseId, long sectionId, long lectureId, String email) {
        if (!checkApprovedInstructor(email)) {
            throw new AccessDeniedException("Giảng viên chưa được duyệt");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));
        if (checkCourseBelongToInstructor(email, course)) {
            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy chương", 1));
            if (section.getCourse().getId() != course.getId()) {
                throw new EmptyResultDataAccessException("Chương không thuộc về khóa học này", 1);
            }
            Lecture lecture = lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy bài giảng", 1));
            return mapper.map(lecture,LectureResponse.class);
        } else
            throw new AccessDeniedException("Bạn không có quyền truy cập");

    }

    @Override
    public LectureResponse createLecture(int courseId, long sectionId, String name, String description, MultipartFile multipartFile, String email) throws IOException {
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
            Lecture lecture = new Lecture();
            lecture.setName(name);
            lecture.setDescription(description);
            lecture.setSection(section);


            String publicId = UUID.randomUUID().toString();

            String url = fileService.uploadFileWithCloudinary(multipartFile, publicId);
            lecture.setPublicId(publicId);
            lecture.setUrl(url);
            // Save lecture
            Lecture newLecture = lectureRepository.save(lecture);
            return mapper.map(newLecture,LectureResponse.class);
        } else
            throw new AccessDeniedException("Bạn không có quyền truy cập");

    }

    @Override
    public LectureResponse updateLectureById(int courseId, long sectionId, long lectureId, String name, String description, MultipartFile multipartFile, String email) throws IOException {
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
            Lecture lecture = lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy bài giảng", 1));
            if(lecture.getSection().getId() != section.getId()) {
                throw new EmptyResultDataAccessException("Bài giảng không thuộc về chương này", 1);
            }
            lecture.setName(name);
            lecture.setDescription(description);

            String publicId = lecture.getPublicId();
            if(multipartFile != null || !multipartFile.isEmpty()) {
                String url = fileService.uploadFileWithCloudinary(multipartFile, publicId);
                lecture.setUrl(url);
            }

            Lecture updatedLecture = lectureRepository.save(lecture);

            return mapper.map(updatedLecture,LectureResponse.class);
        } else
            throw new AccessDeniedException("Bạn không có quyền truy cập");
    }

    @Override
    public List<LectureResponse> getAllLecturesBelongToSectionForStudent(int courseId, long sectionId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        "Người dùng không tồn tại trong hệ thống", 1));
        Student student = user.getStudent();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy chương", 1));
        List<Lecture> lectures =  lectureRepository.findByCourseIdAndSectionId(course.getId(), section.getId());
        Optional<Enrollment> optional = enrollmentRepository.findByCourse(course);
        if (optional.isPresent()) {
            Enrollment enrollment = optional.get();
            if (enrollment.getStudent().getId() != student.getId()) {
                throw new AccessDeniedException("Vui lòng mua khóa học để xem bài giảng");
            }
        } else if (optional.isEmpty()) {
            throw new AccessDeniedException("Vui lòng mua khóa học để xem bài giảng");
        }
        List<LectureResponse> response = new ArrayList<>();
        if(!lectures.isEmpty()) {
            response = lectures.stream()
                    .map(l -> mapper.map(l,LectureResponse.class))
                    .toList();
        }
        return response;
    }

    @Override
    public LectureResponse getLectureByIdForStudent(int courseId, long sectionId, long lectureId, String email) {
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
            if (enrollment.getStudent().getId() != student.getId()) {
                throw new AccessDeniedException("Vui lòng mua khóa học để xem bài giảng");
            }
        } else if (optional.isEmpty()) {
            throw new AccessDeniedException("Vui lòng mua khóa học để xem bài giảng");
        }
        Lecture lectures =  lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy chương", 1));;
        return mapper.map(lectures,LectureResponse.class);
    }


    @Override
    public boolean deleteLectureById(int courseId, long sectionId, long lectureId, String email) {
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
            Lecture lecture = lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy bài giảng", 1));
            if(lecture.getSection().getId() != section.getId()) {
                throw new EmptyResultDataAccessException("Bài giảng không thuộc về chương này", 1);
            }

            String publicId = lecture.getPublicId();
            if (fileService.deleteFileFromCloudinary(publicId)) {
                lectureRepository.delete(lecture);
                return true;
            }
            return false;
        } else
            throw new AccessDeniedException("Bạn không có quyền truy cập");
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
