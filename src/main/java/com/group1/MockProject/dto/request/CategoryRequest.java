package com.group1.MockProject.dto.request;

import com.group1.MockProject.dto.response.SubCategoryDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    private int id;

    @NotBlank(message = "Tên không được để trống")
    @NotNull
    private String name;

    @NotNull
    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    private List<SubCategoryDTO> subcategory = new ArrayList<>();
}
