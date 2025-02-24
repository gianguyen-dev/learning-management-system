package com.group1.MockProject.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddPaymentRequest {
    @NotNull(message = "Vui lòng nhập đầy đủ thông tin")
    private int courseId;
    private LocalDateTime paymentDate = LocalDateTime.now();
}
