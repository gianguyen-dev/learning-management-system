package com.group1.MockProject.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProfileRequest {

    @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
    private String fullName;

    @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
    private String address;

    @NotBlank(message = "Vui lòng điền đầy đủ thông tin")
    @Min(value = 9, message = "Vui lòng điền đầy đủ thông tin")
    private String phone;
}
