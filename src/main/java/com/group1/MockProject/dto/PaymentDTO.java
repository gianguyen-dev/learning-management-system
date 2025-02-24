package com.group1.MockProject.dto;

import lombok.Builder;

public abstract class PaymentDTO {
  @Builder
  public static class VNPayResponse {
    public String paymentUrl;
  }
}
