package com.group1.MockProject.dto.response;


import com.group1.MockProject.entity.Instructor;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageInstructorsDTO {
    private List<InstructorDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
