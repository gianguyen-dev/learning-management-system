package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    @NotNull(message = "Vui lòng đánh giá khóa học này.")
    private int rating;

    @NotBlank(message = "Vui lòng điền đầy đủ thông tin.")
    @Length(max = 255, message = "Tối đa 255 kí tự.")
    private String comment;
}
