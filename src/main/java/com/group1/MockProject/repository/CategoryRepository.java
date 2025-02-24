package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findCategoryById(int id);
}
