package com.group1.MockProject.service;

import com.group1.MockProject.dto.response.InstructorDTO;
import com.group1.MockProject.dto.response.PageInstructorsDTO;
import com.group1.MockProject.entity.Instructor;

import java.util.List;

public interface InstructorService {
    PageInstructorsDTO getAllInstructors(int pageNo, int pageSize, String sortBy, String sortDir);
}
