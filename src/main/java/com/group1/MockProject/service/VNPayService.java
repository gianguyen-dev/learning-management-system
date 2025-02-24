package com.group1.MockProject.service;

import com.group1.MockProject.dto.PaymentDTO;
import com.group1.MockProject.entity.Payment;
import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {
    PaymentDTO.VNPayResponse createVnPayPayment(Payment payment, HttpServletRequest request);
}
