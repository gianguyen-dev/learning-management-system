package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.request.SignInRequest;
import com.group1.MockProject.dto.response.InstructorResponse;
import com.group1.MockProject.dto.response.SignInResponse;
import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.CourseStatus;
import com.group1.MockProject.entity.Instructor;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.dto.response.AdminDashboardResponse;
import com.group1.MockProject.dto.response.CategoryDTO;
import com.group1.MockProject.dto.response.CourseDTO;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.CourseRepository;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.StudentRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.service.AdminService;
import com.group1.MockProject.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public AdminDashboardResponse getDashboardData() {
        try {
            // Thống kê người dùng
            long totalUsers = userRepository.count();
            long totalStudents = studentRepository.count();
            long totalInstructors = userRepository.countByRole(UserRole.INSTRUCTOR);
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
            long newUsersThisMonth = userRepository.countByCreatedDateAfter(startOfMonth);

            // Thống kê khóa học
            long totalCourses = courseRepository.count();

            // Thống kê doanh thu
            double totalRevenue = courseRepository.calculateTotalRevenue();

            // Thống kê đánh giá
            double averageRating = courseRepository.calculateAverageRating();
            long totalReviews = courseRepository.countTotalReviews();

            // Tạo và trả về response
            return AdminDashboardResponse.builder()
                .totalUsers((int)totalUsers)
                .totalStudents((int)totalStudents)
                .totalInstructors((int)totalInstructors)
                .newUsersThisMonth((int)newUsersThisMonth)
                .totalCourses((int)totalCourses)
                .totalRevenue(totalRevenue)
                .averageRating(averageRating)
                .totalReviews((int)totalReviews)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy dữ liệu dashboard: " + e.getMessage());
        }
    }

    @Override
    public User setBlockUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("Người dùng không tồn tại"));
        if (user.getStatus()==1){
            user.setStatus(0);
        }
        return userRepository.save(user);
    }
    @Override
    public User setApprovedUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("Người dùng không tồn tại"));
        if (user.getStatus()==0){
            user.setStatus(1);
        }
        return userRepository.save(user);
    }
    @Override
    public SignInResponse authenticate(SignInRequest request) {

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new BadCredentialsException("Sai email hoặc mật khẩu"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Sai email hoặc mật khẩu");
        }

        // Giả sử bạn sử dụng JWT để tạo token
        String token = JwtUtil.generateToken(user); // Hàm này cần được triển khai riêng

        System.out.println("USER: " + JwtUtil.extractEmail(token));

        return new SignInResponse(token, "Bearer", "Đăng nhập thành công");
    }
}