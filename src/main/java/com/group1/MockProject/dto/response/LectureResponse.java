package com.group1.MockProject.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LectureResponse {
    private long id;
    private String name;
    private String description;
    private String publicId;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
