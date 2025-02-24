package com.group1.MockProject.service;

import com.group1.MockProject.dto.response.LectureResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface LectureService {
    List<LectureResponse> getAllLecturesBelongToSection(int courseId,long sectionId, String email);
    LectureResponse getLectureById(int courseId, long sectionId, long lectureId, String email);

    boolean deleteLectureById(int courseId, long sectionId, long lectureId, String email);

    LectureResponse createLecture(int courseId, long sectionId, String name, String description,MultipartFile multipartFile, String email) throws IOException;

    LectureResponse updateLectureById(int courseId, long sectionId, long lectureId, String name, String description, MultipartFile multipartFile, String email) throws IOException;

    List<LectureResponse> getAllLecturesBelongToSectionForStudent(int courseId,long sectionId, String email);
    LectureResponse getLectureByIdForStudent(int courseId, long sectionId, long lectureId, String email);
}
