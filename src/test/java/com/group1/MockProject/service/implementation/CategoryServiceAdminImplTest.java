package com.group1.MockProject.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import com.group1.MockProject.dto.request.CategoryRequest;
import com.group1.MockProject.dto.response.CategoryDTO;
import com.group1.MockProject.entity.Category;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.entity.UserRole;
import com.group1.MockProject.repository.CategoryRepository;
import com.group1.MockProject.repository.CourseRepository;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.utils.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceAdminImplTest {

  @Mock private ModelMapper modelMapper;

  @Mock private CourseRepository courseRepository;

  @Mock private CategoryRepository categoryRepository;

  @Mock private InstructorRepository instructorRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private CategoryServiceAdminImpl categoryServiceAdminImpl;

  private CategoryRequest categoryRequest;
  private CategoryDTO categoryDTO;
  private Category mockCategory;
  private User mockAdmin;

  @BeforeEach
  void setUp() {
    categoryRequest = new CategoryRequest();
    categoryRequest.setName("Category 1");
    categoryRequest.setDescription("Description 1");

    mockCategory = new Category();
    mockCategory.setName("Category 1");
    mockCategory.setDescription("Description 1");

    categoryDTO = new CategoryDTO();
    categoryDTO.setName("Category 1");
    categoryDTO.setDescription("Description 1");

    mockAdmin = new User();
    mockAdmin.setId(1);
    mockAdmin.setRole(UserRole.ADMIN);
    mockAdmin.setPhone("0909111999");
    mockAdmin.setEmail("admin@group1.com");
    mockAdmin.setFullName("Admin");
    mockAdmin.setStatus(1);
  }

  @Test
  public void testCreateCategory_Success() {
    String mockToken = "mockToken";
    String adminEmail = "mockAdminEmail";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(Mockito.eq(mockToken))).thenReturn(true);
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(adminEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(adminEmail)))
          .thenReturn(Optional.of(mockAdmin));

      Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(mockCategory);

      Mockito.when(modelMapper.map(Mockito.eq(mockCategory), Mockito.eq(CategoryDTO.class)))
          .thenReturn(categoryDTO);

      categoryServiceAdminImpl.createCategory(categoryRequest, mockToken);

      ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
      Mockito.verify(categoryRepository, Mockito.times(1)).save(categoryCaptor.capture());

      Category savedCategory = categoryCaptor.getValue();
      Assertions.assertEquals("Category 1", savedCategory.getName());
      Assertions.assertEquals("Description 1", savedCategory.getDescription());
      Assertions.assertNotNull(savedCategory.getCreatedAt());
    }
  }

  @Test
  public void testCreateCategory_InvalidToken() {
    String mockToken = "mockToken";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.validateToken(Mockito.eq(mockToken)))
          .thenReturn(false);

      Exception exception =
          Assertions.assertThrows(
              BadCredentialsException.class,
              () -> {
                categoryServiceAdminImpl.createCategory(categoryRequest, mockToken);
              });

      Assertions.assertEquals("Token không hợp lệ hoặc đã hết hạn", exception.getMessage());
    }
  }

  @Test
  public void testCreateCategory_UserNotFound() {
    String mockToken = "mockToken";
    String adminEmail = "mockAdminEmail";

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(Mockito.eq(mockToken))).thenReturn(true);
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(adminEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(adminEmail))).thenReturn(Optional.empty());

      Exception exception =
          Assertions.assertThrows(
              EmptyResultDataAccessException.class,
              () -> {
                categoryServiceAdminImpl.createCategory(categoryRequest, mockToken);
              });

      Assertions.assertEquals("Không tìm thấy nguời dùng", exception.getMessage());
    }
  }

  @Test
  public void testCreateCategory_DoNotHaveAccess() {
    String mockToken = "mockToken";
    String adminEmail = "mockAdminEmail";

    mockAdmin.setRole(UserRole.STUDENT);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic.when(() -> JwtUtil.validateToken(Mockito.eq(mockToken))).thenReturn(true);
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(adminEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(adminEmail)))
          .thenReturn(Optional.of(mockAdmin));

      Exception exception =
          Assertions.assertThrows(
              AccessDeniedException.class,
              () -> {
                categoryServiceAdminImpl.createCategory(categoryRequest, mockToken);
              });

      Assertions.assertEquals(
          "Bạn không có quyền để tạo phân loại khóa học", exception.getMessage());
    }
  }

  @Test
  public void testUpdateCategory_Success() {
    String mockToken = "mockToken";
    String adminEmail = "mockAdminEmail";

    mockCategory.setId(1);

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(adminEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(adminEmail)))
          .thenReturn(Optional.of(mockAdmin));
      Mockito.when(categoryRepository.findById(Mockito.anyInt()))
          .thenReturn(Optional.of(mockCategory));
      Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(mockCategory);
      Mockito.when(modelMapper.map(Mockito.eq(mockCategory), Mockito.eq(CategoryDTO.class)))
          .thenReturn(categoryDTO);

      categoryServiceAdminImpl.updateCategory(mockCategory.getId(), categoryRequest, mockToken);

      ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
      Mockito.verify(categoryRepository, Mockito.times(1)).save(categoryCaptor.capture());

      Category savedCategory = categoryCaptor.getValue();
      Assertions.assertEquals("Category 1", savedCategory.getName());
      Assertions.assertEquals("Description 1", savedCategory.getDescription());
    }
  }

  @Test
  public void testUpdateCategory_CategoryNotFound() {
    String mockToken = "mockToken";
    String adminEmail = "mockAdminEmail";
    int categoryId = 1123;

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(adminEmail);

      Mockito.when(categoryRepository.findById(Mockito.eq(categoryId)))
          .thenReturn(Optional.empty());

      Exception exception =
          Assertions.assertThrows(
              EmptyResultDataAccessException.class,
              () ->
                  categoryServiceAdminImpl.updateCategory(categoryId, categoryRequest, mockToken));

      Assertions.assertEquals("Không tìm thấy danh mục", exception.getMessage());
    }
  }

  @Test
  public void testDeleteCategory_Success() {
    String mockToken = "mockToken";
    String adminEmail = "mockAdminEmail";
    int categoryId = 1;

    try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)) {
      jwtUtilMockedStatic
          .when(() -> JwtUtil.extractEmail(Mockito.eq(mockToken)))
          .thenReturn(adminEmail);

      Mockito.when(userRepository.findByEmail(Mockito.eq(adminEmail)))
          .thenReturn(Optional.of(mockAdmin));

      Mockito.when(categoryRepository.findById(Mockito.eq(categoryId)))
          .thenReturn(Optional.of(mockCategory));

      categoryServiceAdminImpl.deleteCategory(categoryId, mockToken);

      Mockito.verify(categoryRepository, Mockito.times(1)).delete(Mockito.eq(mockCategory));
    }
  }
}
