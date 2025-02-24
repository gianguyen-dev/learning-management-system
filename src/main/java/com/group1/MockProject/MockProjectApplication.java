package com.group1.MockProject;

import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.CategoryRepository;
import com.group1.MockProject.repository.CourseRepository;
import com.group1.MockProject.repository.InstructorRepository;
import com.group1.MockProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
public class MockProjectApplication {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private InstructorRepository instructorRepository;
	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	public static void main(String[] args) {
		SpringApplication.run(MockProjectApplication.class, args);
	}
	@Bean
	CommandLineRunner init() {
		return args -> {
			//create and save category
			Category category1 = new Category();
			category1.setName("Technology");
			category1.setDescription("Courses on technology and programming");
			categoryRepository.save(category1);
			// Create and save Instructors
			User instructorUser1 = new User();
			instructorUser1.setEmail("instructor1@example.com");
			instructorUser1.setPassword(passwordEncoder.encode("password1"));
			instructorUser1.setFullName("Instructor One");
			instructorUser1.setAddress("123 Street, City");
			instructorUser1.setPhone("1234567890");
			instructorUser1.setCreatedAt(LocalDateTime.now());
			instructorUser1.setUpdatedAt(LocalDateTime.now());
			instructorUser1.setStatus(0);
			instructorUser1.setRole(UserRole.INSTRUCTOR);
			userRepository.save(instructorUser1);

			Instructor instructor1 = new Instructor();
//			instructor1.setInstructor_code("INS001");
//			instructor1.setFee(500.0);
			instructor1.setName(instructorUser1.getFullName());
			instructor1.setExpertise("5 years in software development");
			instructor1.setUser(instructorUser1);
			instructorRepository.save(instructor1);

			// Create and save Courses
			Course course1 = new Course();
			course1.setTitle("Java Programming");
			course1.setDescription("Learn Java from scratch.");
			course1.setPrice(100000.0);
			course1.setStatus(1);
//			course1.setCodeCourse(UUID.randomUUID().toString());
			course1.setCategory(category1);
			course1.setInstructor(instructor1);
			courseRepository.save(course1);

			//create and save admin
			if (userRepository.findByEmail("nguyenthaison2002@gmail.com").isEmpty()) {
				User userAdmin = new User();
				userAdmin.setEmail("nguyenthaison2002@gmail.com");
				userAdmin.setPassword(passwordEncoder.encode("12122002aA@"));
				userAdmin.setStatus(1);
				userAdmin.setRole(UserRole.ADMIN);
				userRepository.save(userAdmin);
				System.out.println("Admin user created.");
			} else {
				System.out.println("Admin user already exists.");
			}
		};
	}
}
