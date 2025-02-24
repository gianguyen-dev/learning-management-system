package com.group1.MockProject.dto.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartItemDTO {
    private int id;
    private int courseId;
    private String courseName;
    private Double price;
    private Double discountPrice;
}
