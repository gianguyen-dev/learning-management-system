package com.group1.MockProject.service;

import com.group1.MockProject.dto.response.ReviewResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  public List<ReviewResponse> getAllReviews() {
    // Tạo danh sách các đánh giá
    return List.of(
        new ReviewResponse("Khóa học rất hữu ích!", 5, 1),
        new ReviewResponse("Nội dung rõ ràng và dễ hiểu.", 4, 1),
        new ReviewResponse("Giảng viên rất nhiệt tình.", 5, 1),
        new ReviewResponse("Tôi đã học được nhiều điều mới.", 4, 1),
        new ReviewResponse("Cần cải thiện một số phần.", 3, 1));
  }
}
