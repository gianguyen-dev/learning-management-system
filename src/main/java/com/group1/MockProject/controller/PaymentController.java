package com.group1.MockProject.controller;

import com.group1.MockProject.dto.response.GetSavedCourseResponse;
import com.group1.MockProject.service.PaymentService;
import com.group1.MockProject.service.SavedCourseService;
import com.group1.MockProject.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/create_payment/{course_id}")
    public ResponseEntity<?> paymentCourse(@PathVariable("course_id") int courseId) throws UnsupportedEncodingException {
        return paymentService.createPayment(courseId);

    }
    @GetMapping("/vnPay_return")
    public ResponseEntity<String> vnPayReturn(@RequestParam Map<String, String> allParams) {
        paymentService.handleVnPayReturn(allParams);
        return ResponseEntity.ok("success");
    }
    @GetMapping("/create-payment/savedCourses")
    public ResponseEntity<?> paymentSavedCourse(@RequestHeader("Authorization") String authorizationHeader) throws UnsupportedEncodingException {
        String token = authorizationHeader.replace("Bearer ", "");
        String email = JwtUtil.decodeToken(token).getSubject();
        return paymentService.paymentSavedCourse(email);
    }
}
