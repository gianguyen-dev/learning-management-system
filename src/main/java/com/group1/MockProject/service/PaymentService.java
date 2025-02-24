package com.group1.MockProject.service;

import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.request.AddPaymentRequest;
import com.group1.MockProject.dto.request.PaymentRequest;
import com.group1.MockProject.dto.response.AddPaymentResponse;
import com.group1.MockProject.dto.response.PaymentResponse;
import com.group1.MockProject.entity.Payment;
import com.group1.MockProject.entity.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface PaymentService {
  AddPaymentResponse addPaymentDetail(String email, AddPaymentRequest request);

  PaymentResponse callbackPayment(String vnp_TxnRef);

  PaymentResponse callbackPaymentFail(String vnp_TxnRef);

  ResponseEntity<?> createPayment(int idCourse) throws UnsupportedEncodingException;

  Payment checkPayment(String email, PaymentRequest request);

  ResponseEntity<ApiResponseDto<Payment>> handleVnPayReturn(Map<String, String> allParams);

  ResponseEntity<?> paymentSavedCourse(String token) throws UnsupportedEncodingException;

  PaymentResponse freePayment(Payment payment);
}
