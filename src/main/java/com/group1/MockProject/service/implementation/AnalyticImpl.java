package com.group1.MockProject.service.implementation;

import com.group1.MockProject.dto.AnalyticDTO;
import com.group1.MockProject.entity.Analytic;
import com.group1.MockProject.entity.Course;
import com.group1.MockProject.entity.Instructor;
import com.group1.MockProject.entity.User;
import com.group1.MockProject.repository.AnalyticRepository;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.SubscriptionRepository;
import com.group1.MockProject.repository.UserRepository;
import com.group1.MockProject.service.AnalyticService;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class AnalyticImpl implements AnalyticService {
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;
    private final AnalyticRepository analyticRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public AnalyticDTO.AnalyticResponse getInstructorAnalytic(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new EmptyResultDataAccessException("Không tìm thấy tài khoản", 1));
        Instructor instructor = instructorRepository.findInstructorByUser(user).orElseThrow(()->new AccessDeniedException("Bạn không có quyền thực hiện thao tác này"));
        List<Course> courses = instructor.getCourses();
        if(courses.isEmpty()){
            throw new EmptyResultDataAccessException("Không tìm thấy khóa học",1);
        }
        Analytic analytic = analyticRepository.getByInstructor(instructor).orElseGet(()-> {
            Analytic newAnalytic = new Analytic();
            newAnalytic.setInstructor(instructor);
            newAnalytic.setSales(0.0);
            newAnalytic.setVisitorCount(0);
            newAnalytic.setSubscriptionCount(0);
            return analyticRepository.save(newAnalytic);
        });
        double sales = 0.0;
        int subscriptionCount = 0;
        for(Course course : courses){
            sales += course.getPrice()*course.getMyCourses().size();
            subscriptionCount = subscriptionRepository.findByInstructor(instructor).size();
        }
        analytic.setSales(sales);
        analytic.setSubscriptionCount(subscriptionCount);
        return AnalyticDTO.AnalyticResponse.builder().analytic(analytic).build();
    }
}
