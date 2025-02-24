package com.group1.MockProject.service.implementation;

import com.group1.MockProject.config.VNPAYConfig;
import com.group1.MockProject.dto.PaymentDTO;
import com.group1.MockProject.entity.Payment;
import com.group1.MockProject.service.VNPayService;
import com.group1.MockProject.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {
  private final VNPAYConfig vnPayConfig;

  public PaymentDTO.VNPayResponse createVnPayPayment(Payment payment, HttpServletRequest request) {
    long amount = payment.getTotal_price().longValue() * 100L;
    String bankCode = "NCB";
    Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
    vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
    if (bankCode != null && !bankCode.isEmpty()) {
      vnpParamsMap.put("vnp_BankCode", bankCode);
    }
    vnpParamsMap.put("vnp_TxnRef", "" + payment.getPaymentCode());
    vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
    // build query url
    String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
    String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
    String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
    queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
    String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
    return PaymentDTO.VNPayResponse.builder().paymentUrl(paymentUrl).build();
  }
}
