package com.group1.MockProject.service;

import com.group1.MockProject.dto.request.CategoryRequest;
import com.group1.MockProject.dto.response.CategoryDTO;

public interface CategoryService {
    CategoryDTO createCategory(CategoryRequest categoryRequest,String token);

    CategoryDTO updateCategory(int categoryId, CategoryRequest categoryRequest, String token);

    void deleteCategory(int categoryId, String token);
}
