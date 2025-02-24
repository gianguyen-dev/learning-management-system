package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.response.InstructorDTO;
import com.group1.MockProject.dto.response.PageInstructorsDTO;
import com.group1.MockProject.entity.Instructor;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.service.InstructorService;
import com.group1.MockProject.specification.CustomSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InstructorServiceImpl implements InstructorService {

    private InstructorRepository instructorRepository;
    private ModelMapper mapper;

    private Set<String> properties = new HashSet<>(Arrays.asList("id", "expertise", "email", "fullName", "address", "phone", "status", "createdAt", "updatedAt"));

    public InstructorServiceImpl(InstructorRepository instructorRepository, ModelMapper mapper) {
        this.instructorRepository = instructorRepository;
        this.mapper = mapper;
    }
    @Override
    public PageInstructorsDTO getAllInstructors(int pageNo, int pageSize, String sortBy, String sortDir) {

        // check pagination value
        if(pageNo < 0) {
            pageNo = 0;
        }
        if(pageSize < 0) {
            pageSize = 10;
        }
        // check sorting value
        if (!properties.contains(sortBy)) {
            sortBy = "id";
        }
        if (!sortDir.equalsIgnoreCase("asc") || !sortDir.equalsIgnoreCase("desc")) {
            sortDir = "asc";
        }

        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Instructor> page = instructorRepository.findAll(CustomSpecification.sortByCriteria(sortBy,sortDir),pageable);
        List<Instructor> instructors = page.getContent();

        List<InstructorDTO> content = instructors
                .stream()
                .map(instructor -> mapper.map(instructor.getUser(),InstructorDTO.class))
                .toList();

        PageInstructorsDTO response = new PageInstructorsDTO();
        response.setContent(content);
        response.setPageNo(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());
        return response;
    }
}
