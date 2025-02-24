package com.group1.MockProject.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationDTO {
    private int id;
    private String content;
    private String status;
}
