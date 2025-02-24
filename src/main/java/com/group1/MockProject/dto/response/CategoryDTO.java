package com.group1.MockProject.dto.response;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.group1.MockProject.entity.SubCategory;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
public class CategoryDTO {
    private int id;
    private String name;
    private String description;
    private List<SubCategoryDTO> subcategory = new ArrayList<>();
}
