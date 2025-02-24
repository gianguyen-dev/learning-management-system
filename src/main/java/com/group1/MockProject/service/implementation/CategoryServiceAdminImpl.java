package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.CategoryRequest;
import com.group1.MockProject.dto.response.CategoryDTO;
import com.group1.MockProject.entity.Category;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.repository.CategoryRepository;
import com.group1.MockProject.repository.CourseRepository;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.service.CategoryService;
import com.group1.MockProject.utils.JwtUtil;
import java.time.LocalDateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceAdminImpl implements CategoryService {

  private ModelMapper modelMapper;

  private CourseRepository courseRepository;

  private CategoryRepository categoryRepository;

  private InstructorRepository instructorRepository;

  private UserRepository userRepository;

  @Autowired
  public CategoryServiceAdminImpl(
      CourseRepository courseRepository,
      CategoryRepository categoryRepository,
      InstructorRepository instructorRepository,
      UserRepository userRepository,
      ModelMapper modelMapper) {
    this.courseRepository = courseRepository;
    this.categoryRepository = categoryRepository;
    this.instructorRepository = instructorRepository;
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public CategoryDTO createCategory(CategoryRequest categoryRequest, String token) {

    // Xác thực token
    if (!JwtUtil.validateToken(token)) {
      throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
    }

    String email = JwtUtil.extractEmail(token);

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy nguời dùng", 1));

    if (!user.getRole().toString().equals("ADMIN")) {
      throw new AccessDeniedException("Bạn không có quyền để tạo phân loại khóa học");
    }

    // Convert CategoryDTO to Entity
    Category category = new Category();
    category.setName(categoryRequest.getName());
    category.setDescription(categoryRequest.getDescription());
    category.setCreatedAt(LocalDateTime.now());

    // Save to database
    category = categoryRepository.save(category);

    return modelMapper.map(category, CategoryDTO.class);
  }

  @Override
  public CategoryDTO updateCategory(int categoryId, CategoryRequest categoryRequest, String token) {

    // Find the existing category by ID
    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy danh mục", 1));
    //        int instructorId = JwtUtil.extractUserIdFromToken(token);

    String email = JwtUtil.extractEmail(token);

    userRepository
        .findByEmail(email)
        .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    category.setName(categoryRequest.getName());
    category.setDescription(categoryRequest.getDescription());
    category.setUpdatedAt(LocalDateTime.now());

    category = categoryRepository.save(category);

    return modelMapper.map(category, CategoryDTO.class);
  }

  @Override
  public void deleteCategory(int categoryId, String token) {
    String email = JwtUtil.extractEmail(token);

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy người dùng", 1));

    Category category =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khoá học", 1));

    categoryRepository.delete(category);
  }
}
