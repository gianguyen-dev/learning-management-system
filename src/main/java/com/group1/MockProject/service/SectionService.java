package com.group1.MockProject.service;

import com.group1.MockProject.dto.request.SectionRequest;
import com.group1.MockProject.dto.response.SectionResponse;

import java.util.List;

public interface SectionService {
    List<SectionResponse> getAllSection(int courseId, String email);
    SectionResponse getSectionById(int courseId, long sectionId, String email);
    SectionResponse createSection(int courseId, SectionRequest sectionRequest, String email);
    SectionResponse updateSection(int courseId, long sectionId, SectionRequest sectionRequest, String email);

    void deleteSectionById(int courseId, long sectionId, String email);

    <T> List<T> getAllSectionForStudent(int courseId, String email);
    <T> T getSectionByIdForStudent(int courseId, long sectionId, String email);
}
