package com.group1.MockProject.dto.response;

import com.group1.MockProject.entity.Lecture;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SectionResponse {
    private long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LectureResponse> lectures;
}
