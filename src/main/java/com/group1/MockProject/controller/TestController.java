package com.group1.MockProject.controller;

import com.group1.MockProject.repository.*;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/test")
public class TestController {
  private final SavedCourseRepository savedCourseRepository;
  private final InstructorRepository instructorRepository;
  private final UserRepository userRepository;
  private final AnalyticRepository analyticRepository;
  private final SubscriptionRepository subscriptionRepository;

  @GetMapping("/constraint")
  public void throwConstraintViolation() {
    throw new ConstraintViolationException("Validation error", Set.of());
  }

  @GetMapping("/runtime")
  public void throwRuntimeException() {
    throw new RuntimeException("Validation error");
  }


}
