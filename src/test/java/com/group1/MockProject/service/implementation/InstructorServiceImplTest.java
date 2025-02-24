package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.response.InstructorDTO;
import com.group1.MockProject.dto.response.PageInstructorsDTO;
import com.group1.MockProject.entity.Instructor;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.repository.InstructorRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class InstructorServiceImplTest {
  @Mock private InstructorRepository instructorRepository;

  @Mock private ModelMapper mapper;

  @InjectMocks private InstructorServiceImpl instructorService;

  private Instructor mockInstructor;

  @BeforeEach
  void setUp() {
    User mockUser = new User();
    mockUser.setEmail("email@email.com");

    mockInstructor = new Instructor();
    mockInstructor.setUser(mockUser);
    mockUser.setInstructor(mockInstructor);
  }

  @Test
  public void testGetAllInstructor_Success() {
    int pageNo = 1;
    int pageSize = 10;
    String sortBy = "id";
    String sortDir = "asc";

    InstructorDTO instructorDTO = new InstructorDTO();

    Page<Instructor> instructorPage =
        new PageImpl<>(List.of(mockInstructor), PageRequest.of(pageNo, pageSize), 1);

    Mockito.when(
            instructorRepository.findAll(
                Mockito.any(Specification.class), Mockito.any(Pageable.class)))
        .thenReturn(instructorPage);
    Mockito.when(mapper.map(mockInstructor.getUser(), InstructorDTO.class))
        .thenReturn(instructorDTO);

    PageInstructorsDTO result =
        instructorService.getAllInstructors(pageNo, pageSize, sortBy, sortDir);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getPageNo());
    Assertions.assertEquals(10, result.getPageSize());
    Assertions.assertEquals(11, result.getTotalElements());
    Assertions.assertEquals(1, result.getContent().size());
  }

  @Test
  public void testGetAllInstructor_WithInvalidInputs() {
    int pageNo = -1;
    int pageSize = -1;
    String sortBy = "invalid";
    String sortDir = "invalid";

    InstructorDTO instructorDTO = new InstructorDTO();

    Page<Instructor> instructorPage =
        new PageImpl<>(List.of(mockInstructor), PageRequest.of(0, 10), 1);

    Mockito.when(
            instructorRepository.findAll(
                Mockito.any(Specification.class), Mockito.any(Pageable.class)))
        .thenReturn(instructorPage);
    Mockito.when(mapper.map(mockInstructor.getUser(), InstructorDTO.class))
        .thenReturn(instructorDTO);

    PageInstructorsDTO result =
        instructorService.getAllInstructors(pageNo, pageSize, sortBy, sortDir);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(0, result.getPageNo());
    Assertions.assertEquals(10, result.getPageSize());
    Assertions.assertEquals(1, result.getTotalElements());
    Assertions.assertEquals(1, result.getContent().size());
  }
}
